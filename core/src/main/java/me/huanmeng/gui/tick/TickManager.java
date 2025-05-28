package me.huanmeng.gui.tick;

import me.huanmeng.gui.scheduler.Scheduler;
import me.huanmeng.gui.scheduler.Schedulers;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
@SuppressWarnings("unused")
public class TickManager {
    public static Scheduler.@NonNull Task tick(@NonNull Runnable tickle, boolean async, long tick) {
        Scheduler.Task task;
        if (async) {
            task = Schedulers.async().runRepeating(tickle::run, tick, tick);
        } else {
            task = Schedulers.sync().runRepeating(tickle::run, tick, tick);
        }
        return task;
    }

    public static Scheduler.@NonNull Task tick(@NonNull Runnable tickle, @NonNull Scheduler scheduler, long tick) {
        return scheduler.runRepeating(tickle::run, tick, tick);
    }
}
