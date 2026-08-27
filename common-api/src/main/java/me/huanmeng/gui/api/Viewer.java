package me.huanmeng.gui.api;

import java.util.UUID;

/**
 * A platform-independent identity for a player viewing a GUI.
 */
public interface Viewer {
    UUID uniqueId();

    String name();
}
