package me.huanmeng.gui.api.state;

@FunctionalInterface
public interface StateListener<T> {
    void onChange(T previousValue, T currentValue);
}
