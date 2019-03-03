///:FileUtil.java
package cn.weyoung.toolkit.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @author icechen1219
 * @date 2019/01/01
 */
public class FileUtil {
    public static final String UTF8 = "UTF-8";
    public static final String GBK = "GBK";
    public static final String LATIN1 = "ISO-8859-1";
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    public static String readJsonFileAsString(File jsonFile, String charSet) {
        if (charSet == null) {
            charSet = UTF8;
        }
        if (jsonFile == null || !jsonFile.exists()) {
            logger.error(jsonFile + " file not found");
            return null;
        }
        byte[] content = getBytes(jsonFile);
        try {
            return content == null ? "" : new String(content, charSet);
        } catch (UnsupportedEncodingException e) {
            logger.error("不支持的字符编码！", e);
        }
        return "";
    }

    public static String readJsonFileAsString(String jsonFileName, String charSet) {
        return readJsonFileAsString(new File(jsonFileName), charSet);
    }

    public static void saveStringToJsonFile(File jsonFile, String jsonString, String charSet) {
        if (charSet == null) {
            charSet = UTF8;
        }
        if (jsonFile == null) {
            return;
        }
        if (!jsonFile.exists()) {
            if (jsonFile.getParentFile() != null) {
                jsonFile.getParentFile().mkdirs();
            }
            try {
                jsonFile.createNewFile();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }

        // 将字符串写入文件
        Writer write = null;
        try {
            write = new OutputStreamWriter(new FileOutputStream(jsonFile), charSet);
            write.write(jsonString);
            write.flush();
            write.close();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static void saveStringToJsonFile(String jsonFileName, String jsonString, String charSet) {
        saveStringToJsonFile(new File(jsonFileName), jsonString, charSet);
    }

    /**
     * 序列化（将对象写入文件）
     *
     * @throws Exception
     */
    public static void serilize(Object obj, String filename) {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(filename));
            oos.writeObject(obj);
            oos.close();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 反序列化（从文件还原对象）
     *
     * @throws Exception
     */
    public static Object deserilize(String filename) {
        File tmp = new File(filename);
        if (tmp.exists()) {
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(new FileInputStream(filename));
                Object o = ois.readObject();
                return o;
            } catch (IOException | ClassNotFoundException e) {
                logger.error(e.getMessage(), e);
            }

        }
        return null;
    }

    public static File[] listFiles4Dir(String folder, final String extFilter) {
        File file = new File(folder);
        if (file.exists() && file.isDirectory()) {
            if (null == extFilter || extFilter.isEmpty()) {
                return file.listFiles();
            } else {
                return file.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        if (name.endsWith(extFilter)) {
                            return true;
                        }
                        return false;
                    }
                });
            }
        } else {
            return new File[0];
        }
    }

    /**
     * 复制输入流中的数据到指定文件中
     *
     * @param is       输入流
     * @param filePath 文件完整保存路径
     * @throws IOException
     */
    public static void copyFile(InputStream is, String filePath) throws IOException {
        File tmpFile = new File(filePath);
        if (tmpFile.getParentFile() != null) {
            tmpFile.getParentFile().mkdirs();
        }
        // 复制文件
        FileOutputStream fos = new FileOutputStream(filePath);
        byte[] b = new byte[1024 * 1024];
        int length = 0;
        while (-1 != (length = is.read(b))) {
            fos.write(b, 0, length);
        }
        fos.flush();
        fos.close();
        is.close();
    }

    /**
     * 判断file是否不为空文件对象
     *
     * @param file
     * @return
     */
    public static boolean isFileNotEmpty(File file) {
        return file != null && file.exists();
    }

    /**
     * 判断file是否为空文件对象
     *
     * @param file
     * @return
     */
    public static boolean isFileEmpty(File file) {
        return file == null || !file.exists();
    }

    /**
     * 根据byte数组，生成文件
     *
     * @param bfile
     * @param filePath
     * @param fileName
     */
    public static void getFile(byte[] bfile, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            //判断文件目录是否存在
            if (!dir.exists() && dir.isDirectory()) {
                dir.mkdirs();
            }
            file = new File(filePath + "\\" + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    logger.error(e1.getMessage(), e1);
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    logger.error(e1.getMessage(), e1);
                }
            }
        }
    }

    /**
     * 获得指定文件的byte数组
     *
     * @param filePath
     * @return
     */
    public static byte[] getBytes(String filePath) {
        return getBytes(new File(filePath));
    }

    /**
     * 获得指定文件的byte数组
     *
     * @param file
     * @return
     */
    public static byte[] getBytes(File file) {
        byte[] buffer = null;
        try {
            BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file));
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }


        return buffer;
    }
}
///:FileUtil.java
