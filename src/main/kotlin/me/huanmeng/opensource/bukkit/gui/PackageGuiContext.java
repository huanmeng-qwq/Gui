package me.huanmeng.opensource.bukkit.gui;

import org.bukkit.entity.Player;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
@SuppressWarnings("rawtypes")
public class PackageGuiContext {
    private final Player player;
    private AbstractGui gui;

    public PackageGuiContext(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public AbstractGui getGui() {
        return gui;
    }

    public void gui(AbstractGui gui) {
        this.gui = gui;
    }
}
