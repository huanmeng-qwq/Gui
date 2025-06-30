package me.huanmeng.gui.page;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
@FunctionalInterface
public interface IPaginationExecutor<@NonNull T> {
    void print(@Nullable T element);
}
