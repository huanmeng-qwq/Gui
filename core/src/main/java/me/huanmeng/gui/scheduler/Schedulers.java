package me.huanmeng.gui.scheduler;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.Contract;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
public class Schedulers {
    @Nullable
    private static Scheduler sync, async;

    public static void setSync(@NonNull Scheduler sync) {
        Schedulers.sync = sync;
    }

    public static void setAsync(@NonNull Scheduler async) {
        Schedulers.async = async;
    }

    @Contract(value = " -> !null", pure = true)
    public static Scheduler async() {
        return async;
    }

    @Contract(value = " -> !null", pure = true)
    public static Scheduler sync() {
        return sync;
    }
}
