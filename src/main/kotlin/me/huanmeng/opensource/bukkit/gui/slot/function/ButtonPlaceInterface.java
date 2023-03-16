package me.huanmeng.opensource.bukkit.gui.slot.function;

import me.huanmeng.opensource.bukkit.gui.button.Button;
import me.huanmeng.opensource.bukkit.gui.slot.Slot;
import org.bukkit.entity.Player;

@FunctionalInterface
public interface ButtonPlaceInterface {
    boolean tryPlace(Slot slot, Button button, Player player);
}
