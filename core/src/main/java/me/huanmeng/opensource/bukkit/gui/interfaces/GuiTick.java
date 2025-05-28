package me.huanmeng.opensource.bukkit.gui.interfaces;

import me.huanmeng.opensource.scheduler.Scheduler;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
public interface GuiTick extends Runnable {
    /**
     * 线程
     */
    @NonNull
    Scheduler scheduler();

    /**
     * 每多少tick执行一次
     */
    int schedulerTick();
}
