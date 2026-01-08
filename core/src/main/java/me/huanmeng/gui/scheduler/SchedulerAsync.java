package me.huanmeng.gui.scheduler;

import me.huanmeng.gui.GuiManager;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
public class SchedulerAsync implements Scheduler {

    @NonNull
    @Override
    public Task run(@NonNull Runnable runnable) {
        BukkitTaskImpl task = new BukkitTaskImpl(new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        });
        Objects.requireNonNull(task.runnable()).runTaskAsynchronously(GuiManager.instance().plugin());
        return task;
    }

    @NonNull
    @Override
    public Task runRepeating(@NonNull Runnable runnable, long perTick, long timeTick) {
        BukkitTaskImpl task = new BukkitTaskImpl(new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        });
        Objects.requireNonNull(task.runnable()).runTaskTimerAsynchronously(GuiManager.instance().plugin(), perTick, timeTick);
        return task;
    }

    @NonNull
    @Override
    public Task runRepeating(@NonNull Consumer<Task> consumer, long perTick, long timeTick) {
        BukkitTaskImpl task = new BukkitTaskImpl(null) {
            // 没法直接调用self 所以选择了这种
            {
                setRunnable(new BukkitRunnable() {
                    @Override
                    public void run() {
                        consumer.accept(self);
                    }
                });
            }
        };
        Objects.requireNonNull(task.runnable()).runTaskTimerAsynchronously(GuiManager.instance().plugin(), perTick, timeTick);
        return task;
    }

    @NonNull
    @Override
    public Task runLater(@NonNull Runnable runnable, long laterTick) {
        BukkitTaskImpl task = new BukkitTaskImpl(new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        });
        Objects.requireNonNull(task.runnable()).runTaskLaterAsynchronously(GuiManager.instance().plugin(), laterTick);
        return task;
    }

    @Override
    public boolean willStop() {
        return !GuiManager.instance().plugin().isEnabled();
    }
}
