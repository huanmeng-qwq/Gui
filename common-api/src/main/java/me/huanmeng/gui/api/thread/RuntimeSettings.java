package me.huanmeng.gui.api.thread;

import java.util.Objects;

public final class RuntimeSettings {
    private final boolean renderThreadEnabled;
    private final WrongThreadPolicy wrongThreadPolicy;

    public RuntimeSettings(boolean renderThreadEnabled, WrongThreadPolicy wrongThreadPolicy) {
        this.renderThreadEnabled = renderThreadEnabled;
        this.wrongThreadPolicy = Objects.requireNonNull(wrongThreadPolicy, "wrongThreadPolicy");
    }

    public boolean renderThreadEnabled() { return renderThreadEnabled; }
    public WrongThreadPolicy wrongThreadPolicy() { return wrongThreadPolicy; }

    public static RuntimeSettings defaults() {
        return new RuntimeSettings(false, WrongThreadPolicy.THROW);
    }
}
