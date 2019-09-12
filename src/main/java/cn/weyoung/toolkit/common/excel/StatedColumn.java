///:StatedColumn.java
package cn.weyoung.toolkit.common.excel;

import org.apache.poi.ss.usermodel.Cell;

/**
 * 有状态的excel单元格
 *
 * @author icechen1219
 * @date 2019/01/13
 */
public class StatedColumn {
    private Cell cell;
    private boolean merged;
    private int mergedFromRow;
    private int mergedFromColumn;

    public StatedColumn(Cell cell) {
        this(cell, false, -1, -1);
    }

    public StatedColumn(Cell cell, boolean merged, int mergedFromRow, int mergedFromColumn) {
        this.cell = cell;
        this.merged = merged;
        this.mergedFromRow = mergedFromRow;
        this.mergedFromColumn = mergedFromColumn;
    }

    public Cell getCell() {
        return cell;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }

    public boolean isMerged() {
        return merged;
    }

    public void setMerged(boolean merged) {
        this.merged = merged;
    }

    public int getMergedFromRow() {
        return mergedFromRow;
    }

    public void setMergedFromRow(int mergedFromRow) {
        this.mergedFromRow = mergedFromRow;
    }

    public int getMergedFromColumn() {
        return mergedFromColumn;
    }

    public void setMergedFromColumn(int mergedFromColumn) {
        this.mergedFromColumn = mergedFromColumn;
    }
}
///:StatedColumn.java
