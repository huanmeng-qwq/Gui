package me.huanmeng.opensource.bukkit.scheduler;

import me.huanmeng.opensource.bukkit.gui.GuiManager;
import me.huanmeng.opensource.scheduler.Scheduler;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.Consumer;

/**
 * 2022/9/18<br>
 * LimeCode<br>
 *
 * @author huanmeng_qwq
 */
public class SchedulerAsync implements Scheduler {

    @Override
    public Task run(Runnable runnable) {
        BukkitTaskImpl task = new BukkitTaskImpl(new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        });
        task.runnable().runTaskAsynchronously(GuiManager.instance().plugin());
        return task;
    }

    @Override
    public Task runRepeating(Runnable runnable, long perTick, long timeTick) {
        BukkitTaskImpl task = new BukkitTaskImpl(new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        });
        task.runnable().runTaskTimerAsynchronously(GuiManager.instance().plugin(), perTick, timeTick);
        return task;
    }

    @Override
    public Task runRepeating(Consumer<Task> consumer, long perTick, long timeTick) {
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
        task.runnable().runTaskTimerAsynchronously(GuiManager.instance().plugin(), perTick, timeTick);
        return task;
    }

    @Override
    public Task runLater(Runnable runnable, long laterTick) {
        BukkitTaskImpl task = new BukkitTaskImpl(new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        });
        task.runnable().runTaskLaterAsynchronously(GuiManager.instance().plugin(), laterTick);
        return task;
    }

    @Override
    public boolean willStop() {
        return !GuiManager.instance().plugin().isEnabled();
    }
}
