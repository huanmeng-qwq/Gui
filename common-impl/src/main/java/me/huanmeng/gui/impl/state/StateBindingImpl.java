package me.huanmeng.gui.impl.state;

import java.util.Objects;
import me.huanmeng.gui.api.RefreshPolicy;
import me.huanmeng.gui.api.state.State;
import me.huanmeng.gui.api.state.StateBinding;
import me.huanmeng.gui.api.state.Subscription;

public final class StateBindingImpl<T> implements StateBinding<T> {
    private final State<T> state;
    private final RefreshPolicy refreshPolicy;
    private final Subscription subscription;

    public StateBindingImpl(State<T> state, RefreshPolicy refreshPolicy, StateBindingListener<T> listener) {
        this.state = Objects.requireNonNull(state, "state");
        this.refreshPolicy = Objects.requireNonNull(refreshPolicy, "refreshPolicy");
        this.subscription = state.subscribe(listener::onChange);
    }

    @Override public State<T> state() { return state; }
    @Override public RefreshPolicy refreshPolicy() { return refreshPolicy; }
    @Override public void close() { subscription.close(); }

    @FunctionalInterface
    public interface StateBindingListener<T> {
        void onChange(T previous, T current);
    }
}
