package me.huanmeng.gui.scheduler;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.Consumer;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
@SuppressWarnings("unused")
public interface Scheduler {
    /**
     * 执行
     */
    @CanIgnoreReturnValue
    @NonNull
    Task run(@NonNull Runnable runnable);

    /**
     * 重复执行
     *
     * @param runnable runnable
     * @param perTick  第一次执行间隔
     * @param timeTick 每次执行间隔
     * @return task
     */
    @CanIgnoreReturnValue
    @NonNull
    Task runRepeating(@NonNull Runnable runnable, long perTick, long timeTick);

    /**
     * 重复执行
     *
     * @param consumer consumer
     * @param perTick  第一次执行间隔
     * @param timeTick 每次执行间隔
     * @return task
     */
    @CanIgnoreReturnValue
    @NonNull
    Task runRepeating(@NonNull Consumer<Task> consumer, long perTick, long timeTick);

    /**
     * 延迟执行
     *
     * @param runnable  runnable
     * @param laterTick 延迟tick
     * @return task
     */
    @CanIgnoreReturnValue
    @NonNull
    Task runLater(@NonNull Runnable runnable, long laterTick);

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
