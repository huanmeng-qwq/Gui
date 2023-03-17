package me.huanmeng.opensource.bukkit.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
public class MathUtil {
    private MathUtil() {
        throw new UnsupportedOperationException();
    }

    public static int[] range(int min, int max) {
        List<Integer> list = new ArrayList<>();
        for (int i = min; i <= max; i++) {
            list.add(i);
        }
        return list.stream().mapToInt(Integer::intValue).toArray();
    }
}
