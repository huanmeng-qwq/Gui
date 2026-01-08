package me.huanmeng.gui.scheduler;

import me.huanmeng.gui.GuiManager;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.Contract;

/**
 * Static registry for accessing the global synchronous and asynchronous {@link Scheduler} instances.
 * <p>
 * This class serves as a central access point for task scheduling throughout the GUI library.
 * It maintains references to two scheduler implementations:
 * <ul>
 *   <li><b>Sync scheduler</b> - Executes tasks on the main server thread (thread-safe for Bukkit API)</li>
 *   <li><b>Async scheduler</b> - Executes tasks in a separate thread pool (for non-API operations)</li>
 * </ul>
 *
 * <p>
 * <b>Initialization:</b> These schedulers are automatically initialized by {@link GuiManager}
 * during plugin startup. Manual initialization is typically not required.
 *
 * <p>
 * <b>Usage Example:</b>
 * <pre>{@code
 * // Execute on main thread (safe for Bukkit API calls)
 * Schedulers.sync().run(() -> {
 *     player.sendMessage("This runs on the main thread");
 * });
 *
 * // Execute asynchronously (DO NOT use Bukkit API here)
 * Schedulers.async().run(() -> {
 *     // Heavy computation or blocking I/O
 *     String result = expensiveOperation();
 *     // Switch back to sync to use Bukkit API
 *     Schedulers.sync().run(() -> player.sendMessage(result));
 * });
 * }</pre>
 *
 * <p>
 * <b>Thread Safety Warning:</b> Never access the Bukkit API from async tasks. Always switch
 * back to the sync scheduler before calling any Bukkit methods.
 *
 *
 * @author huanmeng_qwq
 * @since 2023/3/17
 * @see Scheduler
 * @see GuiManager
 */
public class Schedulers {
    @Nullable
    private static Scheduler sync, async;

    /**
     * Sets the global synchronous scheduler instance.
     * <p>
     * This method is called internally by {@link GuiManager} during
     * initialization. Manual invocation is typically not required.
     * </p>
     *
     * @param sync the synchronous scheduler to use, never null
     */
    public static void setSync(@NonNull Scheduler sync) {
        Schedulers.sync = sync;
    }

    /**
     * Sets the global asynchronous scheduler instance.
     * <p>
     * This method is called internally by {@link GuiManager} during
     * initialization. Manual invocation is typically not required.
     * </p>
     *
     * @param async the asynchronous scheduler to use, never null
     */
    public static void setAsync(@NonNull Scheduler async) {
        Schedulers.async = async;
    }

    /**
     * Returns the global asynchronous scheduler.
     * <p>
     * Tasks scheduled via this scheduler execute in a separate thread pool and must NOT
     * interact with the Bukkit API. Use this for CPU-intensive operations, file I/O, or
     * network requests.
     * </p>
     * <p>
     * <b>Warning:</b> Accessing Bukkit API from async tasks will cause concurrent modification
     * exceptions and crash the server. Always switch back to {@link #sync()} before using Bukkit API.
     * </p>
     *
     * @return the asynchronous scheduler, never null
     * @throws NullPointerException if called before initialization by {@link GuiManager}
     */
    @Contract(value = " -> !null", pure = true)
    public static Scheduler async() {
        return async;
    }

    /**
     * Returns the global synchronous scheduler.
     * <p>
     * Tasks scheduled via this scheduler execute on the main server thread and are safe
     * for all Bukkit API interactions. This is the default scheduler for most GUI operations.
     * </p>
     *
     * @return the synchronous scheduler, never null
     * @throws NullPointerException if called before initialization by {@link GuiManager}
     */
    @Contract(value = " -> !null", pure = true)
    public static Scheduler sync() {
        return sync;
    }
}
