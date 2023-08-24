package me.huanmeng.opensource.bukkit.tick;

import me.huanmeng.opensource.scheduler.Scheduler;
import me.huanmeng.opensource.scheduler.Schedulers;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
@SuppressWarnings("unused")
public class TickManager {
    public static Scheduler.@NonNull Task tick(@NonNull ITickle tickle, boolean async, long tick) {
        Scheduler.Task task;
        if (async) {
            task = Schedulers.async().runRepeating(tickle::tick, tick, tick);
        } else {
            task = Schedulers.sync().runRepeating(tickle::tick, tick, tick);
        }
        return task;
    }

    public static Scheduler.@NonNull Task tick(@NonNull ITickle tickle, @NonNull Scheduler scheduler, long tick) {
        return scheduler.runRepeating(tickle::tick, tick, tick);
    }
}
