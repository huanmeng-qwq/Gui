package me.huanmeng.gui.func;

import java.util.function.Supplier;

/**
 * 2023/6/4<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
public class FreezeSupplier<T> implements Supplier<T> {
    private final T t;

    public FreezeSupplier(T t) {
        this.t = t;
    }

    public static <T> Supplier<T> of(T t) {
        return new FreezeSupplier<>(t);
    }

    @Override
    public T get() {
        return t;
    }
}
