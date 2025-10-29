package me.huanmeng.gui.scheduler;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.Consumer;

/**
 * Interface for scheduling tasks on either the main server thread or asynchronously.
 * <p>
 * This interface abstracts Bukkit's scheduler API, providing a simplified and consistent way
 * to schedule tasks with different execution patterns:
 * <ul>
 *   <li><b>Immediate execution</b> - {@link #run(Runnable)}</li>
 *   <li><b>Delayed execution</b> - {@link #runLater(Runnable, long)}</li>
 *   <li><b>Repeating execution</b> - {@link #runRepeating(Runnable, long, long)}</li>
 * </ul>
 *
 * <p>
 * <b>Thread Safety:</b> Implementations determine whether tasks execute on the main server thread
 * (synchronous) or in a separate thread pool (asynchronous). Use {@link Schedulers#sync()} for
 * thread-safe operations that interact with the Bukkit API, and {@link Schedulers#async()} for
 * CPU-intensive operations that don't touch the Bukkit API.
 *
 * <p>
 * <b>Usage Example:</b>
 * <pre>{@code
 * // Execute immediately on main thread
 * Schedulers.sync().run(() -> player.sendMessage("Hello!"));
 *
 * // Execute after 5 seconds (100 ticks)
 * Schedulers.sync().runLater(() -> player.sendMessage("Delayed message"), 100L);
 *
 * // Execute every second, starting after 1 second
 * Task task = Schedulers.sync().runRepeating(() -> {
 *     player.sendMessage("Tick!");
 * }, 20L, 20L);
 *
 * // Cancel the repeating task
 * task.stop();
 * }</pre>
 *
 *
 * @author huanmeng_qwq
 * @since 2023/3/17
 * @see Schedulers
 * @see Task
 */
@SuppressWarnings("unused")
public interface Scheduler {
    /**
     * Executes a task immediately (on the next tick for sync schedulers).
     * <p>
     * For synchronous schedulers, the task will execute on the main server thread during
     * the next tick. For asynchronous schedulers, the task will be submitted to a thread pool.
     *
     *
     * @param runnable the task to execute, never null
     * @return a {@link Task} handle that can be used to cancel the execution, never null
     */
    @CanIgnoreReturnValue
    @NonNull
    Task run(@NonNull Runnable runnable);

    /**
     * Schedules a task to execute repeatedly at a fixed rate.
     * <p>
     * The task will first execute after {@code perTick} ticks, then repeatedly execute
     * every {@code timeTick} ticks until cancelled via {@link Task#stop()}.
     *
     * <p>
     * <b>Example:</b>
     * <pre>{@code
     * // Execute every second (20 ticks), starting after 1 second
     * Task task = Schedulers.sync().runRepeating(() -> {
     *     System.out.println("Tick!");
     * }, 20L, 20L);
     * }</pre>
     *
     *
     * @param runnable the task to execute repeatedly, never null
     * @param perTick  the delay in ticks before the first execution (20 ticks = 1 second)
     * @param timeTick the period in ticks between successive executions
     * @return a {@link Task} handle that can be used to cancel the repeating execution, never null
     */
    @CanIgnoreReturnValue
    @NonNull
    Task runRepeating(@NonNull Runnable runnable, long perTick, long timeTick);

    /**
     * Schedules a task to execute repeatedly with access to its own {@link Task} handle.
     * <p>
     * This variant allows the task to cancel itself during execution by calling {@link Task#stop()}
     * on the provided task parameter.
     *
     * <p>
     * <b>Example:</b>
     * <pre>{@code
     * // Execute up to 10 times, then stop
     * AtomicInteger count = new AtomicInteger(0);
     * Schedulers.sync().runRepeating(task -> {
     *     if (count.incrementAndGet() >= 10) {
     *         task.stop();
     *     }
     * }, 20L, 20L);
     * }</pre>
     *
     *
     * @param consumer a consumer that receives the {@link Task} handle and can call {@link Task#stop()}, never null
     * @param perTick  the delay in ticks before the first execution
     * @param timeTick the period in ticks between successive executions
     * @return the {@link Task} handle, never null
     */
    @CanIgnoreReturnValue
    @NonNull
    Task runRepeating(@NonNull Consumer<Task> consumer, long perTick, long timeTick);

    /**
     * Schedules a task to execute after a specified delay.
     * <p>
     * The task will execute once after the specified number of ticks has elapsed.
     *
     * <p>
     * <b>Example:</b>
     * <pre>{@code
     * // Execute after 5 seconds (100 ticks)
     * Schedulers.sync().runLater(() -> {
     *     player.sendMessage("This message is delayed by 5 seconds!");
     * }, 100L);
     * }</pre>
     *
     *
     * @param runnable  the task to execute, never null
     * @param laterTick the delay in ticks before execution (20 ticks = 1 second)
     * @return a {@link Task} handle that can be used to cancel the execution before it runs, never null
     */
    @CanIgnoreReturnValue
    @NonNull
    Task runLater(@NonNull Runnable runnable, long laterTick);

    /**
     * Checks if this scheduler is in the process of being shut down.
     * <p>
     * This method returns {@code true} if the scheduler is stopping (e.g., during plugin disable),
     * which means new tasks may not be accepted or may not execute.
     *
     *
     * @return {@code true} if the scheduler is stopping, {@code false} otherwise
     */
    default boolean willStop() {
        return false;
    }

    /**
     * Represents a handle to a scheduled task that can be cancelled.
     * <p>
     * This interface provides a simple {@link #stop()} method to cancel task execution.
     * For one-time tasks, calling {@code stop()} prevents the task from running if it hasn't
     * executed yet. For repeating tasks, it stops future executions.
     *
     */
    interface Task {
        /**
         * Cancels this task, preventing it from executing or stopping future executions.
         * <p>
         * For tasks that haven't executed yet, this prevents them from running.
         * For repeating tasks, this stops all future executions.
         * </p>
         * <p>
         * This method is idempotent - calling it multiple times has no additional effect.
         * </p>
         */
        void stop();
    }
}
