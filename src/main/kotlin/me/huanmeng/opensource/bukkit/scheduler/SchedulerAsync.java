package me.huanmeng.opensource.bukkit.scheduler;

import me.huanmeng.opensource.bukkit.gui.GuiManager;
import me.huanmeng.opensource.scheduler.Scheduler;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
public class SchedulerAsync implements Scheduler {

    @NotNull
    @Override
    @NonNull
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

    @NotNull
    @Override
    @NonNull
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

    @NotNull
    @Override
    public Task runRepeating(@NotNull Consumer<Task> consumer, long perTick, long timeTick) {
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

    @NotNull
    @Override
    public Task runLater(@NotNull Runnable runnable, long laterTick) {
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
