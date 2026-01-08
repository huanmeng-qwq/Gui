package me.huanmeng.gui.example;

import me.huanmeng.gui.GuiManager;
import me.huanmeng.gui.example.navigation.MainMenuGui;
import me.huanmeng.gui.example.shop.MainShopGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Example plugin demonstrating GUI library features.
 *
 * This plugin provides examples for:
 * - Back navigation between GUIs
 * - Multi-level menu systems (simple and advanced)
 * - Page state preservation
 * - Complex state management through navigation
 * - Deep nesting (up to 6 levels)
 */
public class GuiExamplePlugin extends JavaPlugin {

    private GuiManager guiManager;

    @Override
    public void onEnable() {
        // Initialize the GUI manager
        guiManager = new GuiManager(this);

        getLogger().info("GuiExample plugin enabled!");
        getLogger().info("Commands:");
        getLogger().info("  /guiexample - Simple navigation example (3 levels)");
        getLogger().info("  /shop - Advanced shop system (6 levels)");
    }

    @Override
    public void onDisable() {
        // Close the GUI manager
        if (guiManager != null) {
            guiManager.close();
        }
        getLogger().info("GuiExample plugin disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("guiexample")) {
            // Simple navigation example (3 levels)
            new MainMenuGui(player, true).open();
            return true;
        }

        if (command.getName().equalsIgnoreCase("shop")) {
            // Advanced shop system (6 levels)
            player.sendMessage("§a§l======================");
            player.sendMessage("§6§lAdvanced Shop System");
            player.sendMessage("§a§l======================");
            player.sendMessage("§7This example demonstrates:");
            player.sendMessage("§e• 6-level deep navigation");
            player.sendMessage("§e• Complex state management");
            player.sendMessage("§e• Paginated item browser");
            player.sendMessage("§e• Purchase wizard flow");
            player.sendMessage("§e• Enchantment customization");
            player.sendMessage("§a§l======================");

            new MainShopGui(player, true).open();
            return true;
        }

        return false;
    }
}
