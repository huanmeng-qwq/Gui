package me.huanmeng.gui.func;

import java.util.function.Supplier;

/**
 * A {@link Supplier} implementation that always returns the same frozen (immutable) value.
 * <p>
 * FreezeSupplier wraps a value and returns it on every call to {@link #get()}.
 * This is useful when you need to pass a Supplier but the value is already known
 * and won't change, avoiding the overhead of lambda creation in some cases.
 * <p>
 * <b>Usage Example:</b>
 * <pre>{@code
 * Supplier<String> supplier = FreezeSupplier.of("Hello World");
 * String value = supplier.get(); // Always returns "Hello World"
 * }</pre>
 *
 * @param <T> the type of value supplied
 * @author huanmeng_qwq
 * @since 1.3.3
 */
public class FreezeSupplier<T> implements Supplier<T> {
    /**
     * The frozen value to be returned by this supplier.
     */
    private final T t;

    /**
     * Creates a new FreezeSupplier with the specified value.
     *
     * @param t the value to freeze and return on each {@link #get()} call
     */
    public FreezeSupplier(T t) {
        this.t = t;
    }

    /**
     * Creates a new Supplier that always returns the specified value.
     * <p>
     * This is a convenience factory method for creating FreezeSupplier instances.
     *
     * @param t   the value to freeze
     * @param <T> the type of the value
     * @return a Supplier that always returns the specified value
     */
    public static <T> Supplier<T> of(T t) {
        return new FreezeSupplier<>(t);
    }

    /**
     * Returns the frozen value.
     *
     * @return the value that was provided at construction time
     */
    @Override
    public T get() {
        return t;
    }
}
