package me.huanmeng.opensource.bukkit.gui;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collection;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
@SuppressWarnings("unused")
public class SlotUtil {
    /**
     * 以1开始计算
     * 比如3,5 对应箱子的第3行第5格 = 22
     *
     * @param row    行
     * @param column 列
     * @return bukkit中的slot(0开始)
     */
    public static int getSlot(int row, int column) {
        return (row * 9) - (column == 9 ? 1 : 10 - column);
    }

    /**
     * 通过slot计算行
     *
     * @param slot slot
     * @return 行
     */
    public static int getRow(int slot) {
        return (slot + 9) / 9;
    }

    /**
     * 通过slot计算列
     *
     * @param slot slot
     * @return 列
     */
    public static int getColumn(int slot) {
        return (slot % 9) + 1;
    }

    /**
     * 通过集合数量建议行数
     * <p>
     * 对{@link #getLine(int)}的封装
     *
     * @param items 集合
     */
    public static int getLine(@NonNull Collection<?> items) {
        return getLine(items.size());
    }

    /**
     * 通过数量建议行数
     * <p>
     * 设计该方法的主要目的是用于
     * 有的时候需要将某个list的内容
     * 放到gui中, 则可以使用list#size
     * 经行计算gui所需的行数
     *
     * @param size 数量
     */
    public static int getLine(int size) {
        return (size % 9 == 0 ? size : size + 9) / 9;
    }
}
