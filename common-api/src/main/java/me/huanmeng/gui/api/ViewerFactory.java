package me.huanmeng.gui.api;

public interface ViewerFactory<V> {
    Viewer create(V v);
}
