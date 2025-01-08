package me.huanmeng.opensource.bukkit.gui.button;

import me.huanmeng.opensource.bukkit.gui.AbstractGui;
import me.huanmeng.opensource.bukkit.gui.slot.Slot;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ClickData {
    public final Player player;
    public final AbstractGui<?> gui;
    public final Slot slot;
    @Nullable
    public final Slot formSlot;
    @Nullable
    public final Button button;
    public final InventoryClickEvent event;
    public final ClickType click;
    public final InventoryAction action;
    public final InventoryType.SlotType slotType;
    public final int slotKey;
    public final int hotBarKey;

    public ClickData(Player player, AbstractGui<?> gui, Slot slot, @Nullable Slot formSlot, @Nullable Button button, InventoryClickEvent event, ClickType click, InventoryAction action, InventoryType.SlotType slotType, int slotKey, int hotBarKey) {
        this.player = player;
        this.gui = gui;
        this.slot = slot;
        this.formSlot = formSlot;
        this.button = button;
        this.event = event;
        this.click = click;
        this.action = action;
        this.slotType = slotType;
        this.slotKey = slotKey;
        this.hotBarKey = hotBarKey;
    }

    public Player getPlayer() {
        return player;
    }

    public AbstractGui<?> getGui() {
        return gui;
    }

    public Slot getSlot() {
        return slot;
    }

    public @Nullable Slot getFormSlot() {
        return formSlot;
    }

    @Nullable
    public Button getButton() {
        return button;
    }

    public InventoryClickEvent getEvent() {
        return event;
    }

    public ClickType getClick() {
        return click;
    }

    public InventoryAction getAction() {
        return action;
    }

    public InventoryType.SlotType getSlotType() {
        return slotType;
    }

    public int getSlotKey() {
        return slotKey;
    }

    public int getHotBarKey() {
        return hotBarKey;
    }
}