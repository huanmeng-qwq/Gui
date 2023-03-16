package me.huanmeng.opensource.page;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
@FunctionalInterface
public interface IPaginationExecutor<T> {
    void print(T element);
}
