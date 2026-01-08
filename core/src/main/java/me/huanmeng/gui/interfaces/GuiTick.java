package me.huanmeng.gui.interfaces;

import me.huanmeng.gui.scheduler.Scheduler;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Interface for GUIs that support scheduled tick-based updates.
 * <p>
 * This interface extends {@link Runnable} and provides configuration for periodic execution
 * of GUI updates. Implementing classes can define custom tick behavior by specifying
 * a scheduler and tick interval.
 *
 * <p>
 * <b>Usage Example:</b>
 * <pre>{@code
 * public class MyGui extends AbstractGui<MyGui> implements GuiTick {
 *     @Override
 *     public Scheduler scheduler() {
 *         return Schedulers.sync(); // Run on main thread
 *     }
 *
 *     @Override
 *     public int schedulerTick() {
 *         return 20; // Execute every second (20 ticks)
 *     }
 *
 *     @Override
 *     public void run() {
 *         // Update GUI state
 *         refreshGui();
 *     }
 * }
 * }</pre>
 *
 *
 * @author huanmeng_qwq
 * @since 2023/3/17
 * @see Scheduler
 * @see Runnable
 */
public interface GuiTick extends Runnable {
    /**
     * Returns the scheduler on which this GUI's tick updates should be executed.
     * <p>
     * Common implementations:
     * <ul>
     *   <li>{@link me.huanmeng.gui.scheduler.Schedulers#sync()} - Main server thread (thread-safe)</li>
     *   <li>{@link me.huanmeng.gui.scheduler.Schedulers#async()} - Async thread pool (use with caution)</li>
     * </ul>
     *
     *
     * @return the scheduler for executing tick updates, never null
     */
    @NonNull
    Scheduler scheduler();

    /**
     * Returns the tick interval at which {@link #run()} should be executed.
     * <p>
     * Minecraft runs at 20 ticks per second, so:
     * <ul>
     *   <li>1 tick = 50ms (0.05 seconds)</li>
     *   <li>20 ticks = 1 second</li>
     *   <li>40 ticks = 2 seconds</li>
     * </ul>
     *
     *
     * @return the number of ticks between each execution, must be positive
     */
    int schedulerTick();
}
