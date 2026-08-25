package me.huanmeng.gui.api.state;

/** A handle that removes a previously registered listener. */
@FunctionalInterface
public interface Subscription extends AutoCloseable {
    @Override
    void close();

    static Subscription empty() {
        return () -> { };
    }
}
