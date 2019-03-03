///:SecurityUtil.java
package cn.weyoung.toolkit.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

import static cn.weyoung.toolkit.util.StringUtil.DEFAULT_CHAR_SET;
import static cn.weyoung.toolkit.util.StringUtil.isStrEmpty;

/**
 * @author icechen1219
 * @date 2019/02/09
 */
public class SecurityUtil {
    /**
     * 给md5加盐的混淆字符串
     */
    private static final String SALT = "&%5123***&&%%$$#@";
    private static Logger logger = LoggerFactory.getLogger(SecurityUtil.class);

    /**
     * 字符串MD5加密
     *
     * @param str
     * @return
     */
    public static String buildMd5(String str) {
        String base = str + "/" + SALT;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    /**
     * 字符串base64加密
     *
     * @param oldStr
     * @param charset
     * @return
     */
    public static String encodeBase64(String oldStr, String charset) {
        // 对字节数组Base64编码
        final Base64.Encoder encoder = Base64.getEncoder();
        try {
            if (isStrEmpty(charset)) {
                charset = DEFAULT_CHAR_SET;
            }
            String base64 = encoder.encodeToString(oldStr.getBytes(charset));
            logger.debug(base64);
            return base64;
        } catch (UnsupportedEncodingException e) {
            logger.error("不支持的字符编码！", e);
        }
        return "";
    }

    /**
     * 将base64字符串解密
     *
     * @param base64
     * @param charset
     * @return
     */
    public static String decodeBase64(String base64, String charset) {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] bytes = decoder.decode(base64);
        try {
            return new String(bytes, charset);
        } catch (UnsupportedEncodingException e) {
            logger.error("不支持的字符编码！", e);
        }
        return "";
    }

}
///:SecurityUtil.java
