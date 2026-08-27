package me.huanmeng.gui.api;

public enum ClickResult {
    CANCEL,
    ALLOW(false),
    REFRESH,
    CLOSE;
    private boolean cancelEvent = true;

    private ClickResult() {
    }

    private ClickResult(boolean cancelEvent) {
        this.cancelEvent = cancelEvent;
    }

    public boolean isCancelEvent() {
        return cancelEvent;
    }
}
