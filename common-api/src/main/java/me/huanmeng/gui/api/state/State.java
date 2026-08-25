package me.huanmeng.gui.api.state;

public interface State<T> {
    T getValue();
    void setValue(T value);
    Subscription subscribe(StateListener<? super T> listener);

    static <T> State<T> of(T value) {
        return new DefaultState<T>(value);
    }
}
