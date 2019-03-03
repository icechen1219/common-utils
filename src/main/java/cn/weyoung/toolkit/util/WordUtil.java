///:WordUtil.java
package cn.weyoung.toolkit.util;

import org.apache.commons.compress.utils.Lists;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author icechen1219
 * @date 2019/01/13
 */
public class WordUtil {
    private static Logger logger = LoggerFactory.getLogger(WordUtil.class);

    public static <T> List<String> readWordFile(String path) {
        List<String> contextList = Lists.newArrayList();
        InputStream stream = null;
        try {
            stream = new FileInputStream(new File(path));
            if (path.endsWith(".doc")) {
                HWPFDocument document = new HWPFDocument(stream);
                WordExtractor extractor = new WordExtractor(document);
                String[] contextArray = extractor.getParagraphText();
                for (String context : contextArray) {
                    if (context.trim().isEmpty()) {
                        continue;
                    }
                    contextList.add(context);
                }
                extractor.close();
                document.close();
            } else if (path.endsWith(".docx")) {
                XWPFDocument document = new XWPFDocument(stream).getXWPFDocument();
                List<XWPFParagraph> paragraphList = document.getParagraphs();
                for (XWPFParagraph xwpfParagraph : paragraphList) {
                    // 忽略空行
                    String paragraphText = xwpfParagraph.getParagraphText();
                    if (paragraphText.trim().isEmpty()) {
                        continue;
                    }
                    contextList.add(paragraphText);
                }
                document.close();
            } else {
                logger.debug("此文件{}不是word文件", path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != stream) {
                try {
                    stream.close();
                } catch (IOException e) {
                    logger.error("读取word文件失败", e);
                }
            }
        }
        return contextList;
    }

    public static void main(String[] args) {
        String xlsFile = System.getProperty("user.dir") + "/excel/" + "2016年下半年软件设计师考试上午真题(上午题).docx";
        List<String> strings = readWordFile(xlsFile);
        for (String string : strings) {
            logger.debug(string);
            logger.debug("====================================");
        }
    }

}
///:WordUtil.java
