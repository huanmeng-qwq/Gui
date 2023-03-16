package me.huanmeng.opensource.bukkit.gui.interfaces;

import me.huanmeng.opensource.bukkit.tick.ITickle;
import me.huanmeng.opensource.scheduler.Scheduler;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
public interface GuiTick extends ITickle {
    /**
     * 线程
     */
    Scheduler scheduler();

    /**
     * 每多少tick执行一次
     */
    int schedulerTick();
}
