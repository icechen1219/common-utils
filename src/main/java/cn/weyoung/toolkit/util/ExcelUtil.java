///:ExcelUtil.java
package cn.weyoung.toolkit.util;

import cn.weyoung.toolkit.common.annotation.ExcelField;
import cn.weyoung.toolkit.common.excel.StatedColumn;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;

/**
 * @author icechen1219
 * @date 2019/01/13
 */
public class ExcelUtil {
    private static final String TITLE_SEPERATOR = "/";
    private static Logger logger = LoggerFactory.getLogger(ExcelUtil.class);
    private static Map<String, String> cellTitleMap = new HashMap<>(16);

    /**
     * 单表头excel转MapList
     *
     * @param xlsFile
     * @return
     */
    public static List<Map<String, Object>> singleHeaderToMapList(String xlsFile) {
        return multiHeaderToMapList(xlsFile, 1);
    }

    /**
     * 多表头excel转MapList，只保留最后一层表头
     *
     * @param xlsFile
     * @param headerRow
     * @return
     */
    public static List<Map<String, Object>> multiHeaderToMapList(String xlsFile, int headerRow) {
        List<Map<String, Object>> mapList = new ArrayList<>();

        File file = new File(xlsFile);
        if (!file.exists()) {
            logger.error(xlsFile + " is not exist");
            return mapList;
        }

        Workbook workbook = guessWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.rowIterator();
        int numMergedRegions = sheet.getNumMergedRegions();
        logger.debug("merge cells: " + numMergedRegions);

        // 第headerRow行为标题行
        Row titleRow = null;
        while (headerRow-- > 0) {
            titleRow = rowIterator.next();
        }
        // 返回表头的实际列数
        short lastCellNum = titleRow.getLastCellNum();
        logger.info(rowToString(titleRow, TITLE_SEPERATOR));

        while (rowIterator.hasNext()) {
            Row currRow = rowIterator.next();
            // 每行对应一个Map
            Map<String, Object> columnMap = new HashMap<>(lastCellNum);

            // 用for循环取代迭代器的目的是为了避免跳过中间的空单元格
            StringBuffer sb = new StringBuffer(64);
            for (int i = 0; i < lastCellNum; i++) {
                String title = titleRow.getCell(i).toString();
                // 表头为空，会导致数据混乱，所以直接跳过无表头的数据
                if (title.trim().isEmpty()) {
                    continue;
                }
                Cell cell = currRow.getCell(i);
                StatedColumn statedColumn = guessMergedRegion(cell);
                Object cellValue = statedColumn.getCellValue();
                sb.append(cellValue).append(TITLE_SEPERATOR);
                // 每个单元格一个key-value
                columnMap.put(title, cellValue);
            }
            logger.debug(sb.toString());

            mapList.add(columnMap);
        }

        return mapList;
    }

    /**
     * 根据文件后缀名，创建不同的workbook对象
     *
     * @param file
     * @return
     */
    public static Workbook guessWorkbook(File file) {
        if (file != null && file.exists()) {
            try {
                if (file.getName().endsWith(".xls")) {
                    return new HSSFWorkbook(new FileInputStream(file));
                } else {
                    return new XSSFWorkbook(new FileInputStream(file));
                }
            } catch (IOException ex) {
                logger.error(ex.getMessage(), ex);
            }
        }

        return null;
    }

    /**
     * 获取单元格的真实值
     *
     * @param cell
     * @return
     */
    public static Object getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case BLANK:
                return "";
            case FORMULA:
                // 如果单元格包含公式，则调用科学函数计算该单元格，返回计算后的值
                Workbook workbook = cell.getSheet().getWorkbook();
                FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
                Cell formulaCell = formulaEvaluator.evaluateInCell(cell);
                return formulaCell.getNumericCellValue();
            case NUMERIC:
                // poi4将日期单元格也按数值计算，所以需要判断
                if (DateUtil.isCellDateFormatted(cell)) {
                    logger.debug("found date cell at({},{})", cell.getRowIndex() + 1, cell.getColumnIndex() + 1);
                    return cell.getDateCellValue();
                }

                return cell.getNumericCellValue();
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case ERROR:
                return ErrorEval.getText(cell.getErrorCellValue());
            default:
                return "Unknown Cell Type: " + cell.getCellType();
        }
    }

    /**
     * 根据坐标判断单元格是否为合并单元格
     *
     * @param sheet
     * @param row
     * @param column
     * @return
     */
    public static StatedColumn guessMergedRegion(Sheet sheet, int row, int column) {
        int sheetMergeCount = sheet.getNumMergedRegions();
        Cell cell = sheet.getRow(row).getCell(column);
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            int firstColumn = range.getFirstColumn();
            int lastColumn = range.getLastColumn();
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();
            if (row >= firstRow && row <= lastRow) {
                if (column >= firstColumn && column <= lastColumn) {
                    return new StatedColumn(cell, true, firstRow, firstColumn);
                }
            }
        }
        return new StatedColumn(cell);
    }

    /**
     * 判断某个单元格是否为合并单元格
     *
     * @param cell
     * @return
     */
    public static StatedColumn guessMergedRegion(Cell cell) {
        if (cell == null) {
            return new StatedColumn(null);
        }
        Sheet sheet = cell.getSheet();
        int row = cell.getRowIndex();
        int column = cell.getColumnIndex();
        return guessMergedRegion(sheet, row, column);
    }

    /**
     * 获取一行的字符串形式
     *
     * @param row
     * @return
     */
    public static String rowToString(Row row, String seperator) {
        StringBuffer sb = new StringBuffer(64);
        Iterator<Cell> cellIterator = row.cellIterator();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            sb.append(cell).append(seperator);
        }
        return sb.toString();
    }

    public static <E> E mappingToObject(Class<E> entityClass, Map<String, Object> resultMap) {

        E instance = null;
        try {
            instance = entityClass.getConstructor().newInstance();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return null;
        }

        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            String cellTitle = cellTitleMap.get(entityClass.getName() + field.getName());
            if (cellTitle == null) {
                cellTitle = guessCellTitle(field);
                cellTitleMap.put(entityClass.getName() + field.getName(), cellTitle);
            }

            Object value = resultMap.get(cellTitle);
            field.setAccessible(true);

            // 针对日期格式的特殊检测
            if (field.getType().equals(Date.class) && value.getClass().equals(String.class)) {
                String dateStr = (String) value;
                if (dateStr.isEmpty()) {
                    logger.debug("found null date...");
                    continue;
                }
                Date date = null;
                try {
                    date = DateFormat.getDateInstance().parse(dateStr);
                    value = date;
                } catch (ParseException ex) {
                    logger.error("found unformatted date...", ex);
                    continue;
                }

            }
            try {
                field.set(instance, value);
            } catch (IllegalAccessException ex) {
                logger.error(ex.getMessage(), ex);
            }

        }
        return instance;
    }

    public static <E> List<E> mappingToList(Class<E> entityClass, List<Map<String, Object>> mapList) {
        List<E> resultList = new ArrayList<>(mapList.size());
        for (Map<String, Object> resultMap : mapList) {
            resultList.add(mappingToObject(entityClass, resultMap));
        }

        return resultList;
    }

    /**
     * 猜excel单元格标题
     *
     * @param field
     * @return
     */
    private static String guessCellTitle(Field field) {
        ExcelField excelField = field.getAnnotation(ExcelField.class);
        if (excelField != null) {
            String colummName = excelField.value();
            if (!colummName.isEmpty()) {
                return colummName;
            }
            colummName = excelField.name();
            if (!colummName.isEmpty()) {
                return colummName;
            }
        }
        return StringUtil.camelToUnderline(field.getName());
    }

}
///:ExcelUtil.java
