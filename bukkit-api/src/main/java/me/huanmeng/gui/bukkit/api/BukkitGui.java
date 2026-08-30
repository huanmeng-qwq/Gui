package me.huanmeng.gui.bukkit.api;

import me.huanmeng.gui.api.Gui;
import me.huanmeng.gui.api.layout.Slot;
import java.util.Collection;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public interface BukkitGui extends Gui, InventoryHolder {
    Player player();

    void player(Player player);

    Collection<Player> players();

    void open(Player player);

    void close(Player player);

    Inventory inventory(Player player);

    String title();

    void title(String title);

    void title(Component title);

    void openInventory();


    @Override
    default @NotNull Inventory getInventory() {
        final Inventory inventory = inventory();
        if (inventory == null) {
            throw new IllegalStateException("inventory is null");
        }
        return inventory;
    }

    BukkitGuiRuntime runtime();

    Inventory inventory();

    BukkitButton buttonAt(int slot);

    BukkitButton buttonAt(Slot slot);

    boolean cancelEmptyClicks();

    boolean cancelBottomClicks();

    @Override
    BukkitGui copy();
}
