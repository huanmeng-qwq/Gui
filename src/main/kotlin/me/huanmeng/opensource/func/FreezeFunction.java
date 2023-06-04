package me.huanmeng.opensource.func;

import java.util.function.Function;

/**
 * 2023/6/4<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
public class FreezeFunction<T, R> implements Function<T, R> {
    private final R result;

    public FreezeFunction(R r) {
        this.result = r;
    }

    public static <T, R> Function<T, R> of(R r) {
        return new FreezeFunction<>(r);
    }

    @Override
    public R apply(T t) {
        return result;
    }
}
