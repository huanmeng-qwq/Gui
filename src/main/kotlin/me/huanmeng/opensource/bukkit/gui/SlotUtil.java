package me.huanmeng.opensource.bukkit.gui;

import java.util.Collection;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
public class SlotUtil {
    /**
     * 以1开始计算
     * 比如3,5 对应箱子的第3行第5格 = 22
     *
     * @param line   行数
     * @param column 列
     * @return bukkit中的slot(0开始)
     */
    public static int getSlot(int line, int column) {
        return (line * 9) - (column == 9 ? 1 : 10 - column);
    }

    public static int getLine(Collection<?> items) {
        return (items.size() % 9 == 0 ? items.size() : items.size() + 9) / 9;
    }

    public static int getLine(int size) {
        return (size % 9 == 0 ? size : size + 9) / 9;
    }
}
