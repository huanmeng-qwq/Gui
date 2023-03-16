package me.huanmeng.opensource.bukkit.scheduler;

import me.huanmeng.opensource.scheduler.Scheduler;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
public class BukkitTaskImpl implements Scheduler.Task {
    protected Scheduler.Task self;
    private BukkitRunnable runnable;

    public BukkitTaskImpl(BukkitRunnable runnable) {
        this.runnable = runnable;
        this.self = this;
    }

    @Override
    public void stop() {
        runnable.cancel();
    }

    public BukkitRunnable runnable() {
        return runnable;
    }

    public BukkitTaskImpl setRunnable(BukkitRunnable runnable) {
        this.runnable = runnable;
        return this;
    }
}
