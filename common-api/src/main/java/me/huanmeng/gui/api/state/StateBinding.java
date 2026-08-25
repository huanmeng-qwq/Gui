package me.huanmeng.gui.api.state;

import me.huanmeng.gui.api.RefreshPolicy;

public interface StateBinding<T> extends Subscription {
    State<T> state();
    RefreshPolicy refreshPolicy();
}
