package me.huanmeng.opensource.scheduler;

import java.util.function.Consumer;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
public interface Scheduler {
    Task run(Runnable runnable);

    Task runRepeating(Runnable runnable, long perTick, long timeTick);

    Task runRepeating(Consumer<Task> consumer, long perTick, long timeTick);

    Task runLater(Runnable runnable, long laterTick);

    default boolean willStop() {
        return false;
    }

    interface Task {
        void stop();
    }
}
