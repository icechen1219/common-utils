package cn.weyoung.toolkit.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * @author icechen1219
 */
public class UUIDUtil {
    private static Logger logger = LoggerFactory.getLogger(UUIDUtil.class);

    /**
     * @return 12位随机字符串
     */
    public static String getRandomNum() {
        String s = UUID.randomUUID().toString();
        logger.debug(s);
        s = s.substring(0, 8) + s.substring(9, 13) + s.substring(14, 18) + s.substring(19, 23) + s.substring(24);
        return s.substring(0, 12);
    }

    public static void main(String[] args) {
        logger.debug(getRandomNum());
    }
}
