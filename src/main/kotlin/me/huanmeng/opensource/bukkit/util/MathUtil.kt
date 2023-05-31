package me.huanmeng.opensource.bukkit.util

/**
 * 2023/3/17<br></br>
 * Gui<br></br>
 *
 * @author huanmeng_qwq
 */
object MathUtil {
    /**
     * 获取一个范围内的随机数
     *
     * @param min 最小值
     * @param max 最大值
     * @return 数组
     */
    @JvmStatic
    fun range(min: Int, max: Int): IntArray {
        return min.rangeTo(max).toList().toIntArray()
    }
}
