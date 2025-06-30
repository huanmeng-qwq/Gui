package me.huanmeng.gui.gui.impl;

import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * 2024/12/28<br>
 * Bukkit-Gui-pom<br>
 *
 * @author huanmeng_qwq
 */
public class CustomGuiPage extends AbstractGuiPage<CustomGuiPage>{
    public CustomGuiPage(@NonNull Player player) {
        super(player);
    }

    public CustomGuiPage() {
        super();
    }

    @Override
    protected @NonNull CustomGuiPage self() {
        return this;
    }

    @Override
    protected @NonNull CustomGuiPage newGui() {
        return new CustomGuiPage();
    }

    @Override
    public CustomGuiPage copy() {
        return (CustomGuiPage) super.copy();
    }
}
