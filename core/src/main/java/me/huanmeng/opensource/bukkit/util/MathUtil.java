package me.huanmeng.opensource.bukkit.util;

import java.util.stream.IntStream;

/**
 * 2023/8/24<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
public final class MathUtil {
    /**
     * 获取一个范围内的随机数
     *
     * @param min 最小值
     * @param max 最大值
     * @return 数组
     */
    public static int[] range(int min, int max) {
        return IntStream.range(min, max).toArray();
    }
}
