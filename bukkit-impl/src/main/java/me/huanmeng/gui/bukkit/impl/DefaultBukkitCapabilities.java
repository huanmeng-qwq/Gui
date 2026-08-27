package me.huanmeng.gui.bukkit.impl;

import me.huanmeng.gui.bukkit.api.BukkitCapabilities;
import org.bukkit.Server;

final class DefaultBukkitCapabilities implements BukkitCapabilities {
    private final Server server;

    DefaultBukkitCapabilities(Server server) {
        this.server = server;
    }

    @Override
    public boolean paper() {
        return classPresent("com.destroystokyo.paper.PaperConfig")
                || classPresent("io.papermc.paper.configuration.Configuration");
    }

    @Override
    public boolean offhandClick() {
        return enumPresent("org.bukkit.event.inventory.ClickType", "SWAP_OFFHAND");
    }

    @Override
    public boolean adventureTitles() {
        return classPresent("net.kyori.adventure.text.Component");
    }

    @Override
    public String serverVersion() {
        return server.getVersion();
    }

    private static boolean classPresent(String name) {
        try {
            Class.forName(name, false, DefaultBukkitCapabilities.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static boolean enumPresent(String type, String value) {
        try {
            Enum.valueOf((Class) Class.forName(type), value);
            return true;
        } catch (RuntimeException | ClassNotFoundException ignored) {
            return false;
        }
    }
}
