package me.huanmeng.opensource.bukkit.gui.button;

import me.huanmeng.opensource.bukkit.gui.button.function.PlayerClickCancelInterface;
import me.huanmeng.opensource.bukkit.gui.button.function.PlayerClickInterface;
import me.huanmeng.opensource.bukkit.gui.button.function.PlayerSimpleCancelInterface;
import me.huanmeng.opensource.bukkit.gui.button.function.UserItemInterface;
import me.huanmeng.opensource.bukkit.gui.enums.Result;
import me.huanmeng.opensource.bukkit.gui.slot.Slot;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
@FunctionalInterface
public interface Button {
    ItemStack getShowItem(Player player);

    Button EMPTY = (p) -> null;

    default Result onClick(Slot slot, Player player, ClickType click, InventoryAction action, InventoryType.SlotType slotType, int slotKey, int hotBarKey, InventoryClickEvent e) {
        return Result.CANCEL;
    }

    static Button of(UserItemInterface item) {
        return new EmptyButton(item);
    }

    static Button of(UserItemInterface item, PlayerClickInterface clickable) {
        return new ClickButton(item, clickable);
    }

    static Button of(UserItemInterface item, PlayerClickCancelInterface clickable) {
        return new ClickButton(item, clickable);
    }

    static Button of(UserItemInterface item, PlayerSimpleCancelInterface playerSimpleCancelInterface) {
        return new ClickButton(item, playerSimpleCancelInterface);
    }

    static <IMPL extends UserItemInterface & PlayerClickInterface> Button ofInstance(IMPL impl) {
        return new ClickButton(impl, impl);
    }

    static <IMPL extends UserItemInterface & PlayerClickInterface> List<Button> ofInstances(Collection<IMPL> impl) {
        return impl.stream().map(Button::ofInstance).collect(Collectors.toList());
    }

    static <IMPL extends UserItemInterface & PlayerClickInterface> List<Button> ofInstancesArray(IMPL... impl) {
        return Arrays.stream(impl).map(Button::ofInstance).collect(Collectors.toList());
    }

    static List<Button> ofItemStacksButton(Collection<ItemStack> itemStacks, Function<ItemStack, Button> map) {
        return itemStacks.stream().map(map).collect(Collectors.toList());
    }

    static List<Button> ofItemStacks(Collection<ItemStack> itemStacks, Function<ItemStack, UserItemInterface> map) {
        return itemStacks.stream().map(map).map(Button::of).collect(Collectors.toList());
    }

    static List<Button> ofItemStacks(Collection<ItemStack> itemStacks) {
        return itemStacks.stream().map(e -> (UserItemInterface) player -> e).map(Button::of).collect(Collectors.toList());
    }

    static <IMPL extends UserItemInterface & PlayerClickInterface> List<Button> ofItemStacksInstances(Collection<ItemStack> itemStacks, Function<ItemStack, IMPL> map) {
        return itemStacks.stream().map(map).map(Button::ofInstance).collect(Collectors.toList());
    }

    static Button empty() {
        return EMPTY;
    }
}
