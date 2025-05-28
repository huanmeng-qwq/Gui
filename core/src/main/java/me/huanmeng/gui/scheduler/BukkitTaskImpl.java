package me.huanmeng.gui.scheduler;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
public class BukkitTaskImpl implements Scheduler.Task {
    protected Scheduler.@NonNull Task self;
    @Nullable
    private BukkitRunnable runnable;

    public BukkitTaskImpl(@Nullable BukkitRunnable runnable) {
        this.runnable = runnable;
        this.self = this;
    }

    @Override
    public void stop() {
        if (runnable != null) {
            runnable.cancel();
        }
    }

    @Nullable
    public BukkitRunnable runnable() {
        return runnable;
    }

    @NonNull
    @CanIgnoreReturnValue
    public BukkitTaskImpl setRunnable(@NonNull BukkitRunnable runnable) {
        this.runnable = runnable;
        return this;
    }
}
