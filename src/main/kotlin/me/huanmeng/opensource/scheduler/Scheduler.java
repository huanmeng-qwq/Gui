package me.huanmeng.opensource.scheduler;

import com.google.errorprone.annotations.CanIgnoreReturnValue;

import java.util.function.Consumer;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
public interface Scheduler {
    /**
     * 执行
     */
    @CanIgnoreReturnValue
    Task run(Runnable runnable);

    /**
     * 重复执行
     *
     * @param runnable runnable
     * @param perTick  第一次执行间隔
     * @param timeTick 每次执行间隔
     * @return task
     */
    @CanIgnoreReturnValue
    Task runRepeating(Runnable runnable, long perTick, long timeTick);

    /**
     * 重复执行
     *
     * @param consumer consumer
     * @param perTick  第一次执行间隔
     * @param timeTick 每次执行间隔
     * @return task
     */
    @CanIgnoreReturnValue
    Task runRepeating(Consumer<Task> consumer, long perTick, long timeTick);

    /**
     * 延迟执行
     *
     * @param runnable  runnable
     * @param laterTick 延迟tick
     * @return task
     */
    @CanIgnoreReturnValue
    Task runLater(Runnable runnable, long laterTick);

    /**
     * 是否被禁用
     */
    default boolean willStop() {
        return false;
    }

    interface Task {
        /**
         * 停止
         */
        void stop();
    }
}
