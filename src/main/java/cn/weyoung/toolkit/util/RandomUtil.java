///:RandomUtil.java
package cn.weyoung.toolkit.util;

import java.util.Random;

/**
 * @author icechen1219
 * @date 2019/02/09
 */
public class RandomUtil {
    private static Random rd = new Random();

    /**
     * 产生一定范围内的随机整数
     *
     * @param min
     * @param max
     * @return
     */
    public static int randRange(int min, int max) {
        return rd.nextInt(max - min) + min;
    }

    /**
     * 返回4位随机整数
     *
     * @return
     */
    public static int randFourBit() {
        return randRange(1000, 10000);
    }
}
///:RandomUtil.java
