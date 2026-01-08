package me.huanmeng.gui.util;

import me.huanmeng.gui.SlotUtil;

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

    public static int[] cut(int a1, int a2, int b1, int b2) {
        if (a1 > a2 || b1 > b2) {
            throw new IllegalArgumentException("Invalid interval: start should be less than or equal to end.");
        }

        int rowCount = a2 - a1 + 1;
        int colCount = b2 - b1 + 1;
        int totalSlots = rowCount * colCount;

        int[] slots = new int[totalSlots];
        int index = 0;

        for (int row = a1; row <= a2; row++) {
            for (int col = b1; col <= b2; col++) {
                slots[index++] = SlotUtil.getSlot(row, col);
            }
        }

        return slots;
    }
}
