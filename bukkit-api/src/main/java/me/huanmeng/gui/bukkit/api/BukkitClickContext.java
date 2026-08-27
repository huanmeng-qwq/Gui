package me.huanmeng.gui.bukkit.api;

import me.huanmeng.gui.api.ClickContext;
import me.huanmeng.gui.api.layout.Slot;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public interface BukkitClickContext extends ClickContext {
    @Override
    BukkitGui gui();

    @Override
    BukkitViewer viewer();

    Player player();

    @Override
    Slot slot();

    @Override
    BukkitButton button();

    int rawSlot();

    InventoryClickEvent event();
}
