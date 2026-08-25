package me.huanmeng.gui.api.thread;

public final class WrongThreadException extends IllegalStateException {
    public WrongThreadException(String message) {
        super(message);
    }
}
