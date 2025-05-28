package me.huanmeng.gui.gui.impl;

import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
public class GuiCustom extends AbstractGuiCustom<@NonNull GuiCustom> {

    public GuiCustom(@NonNull Player player) {
        super(player);
    }

    public GuiCustom() {
        super();
    }

    @Override
    protected @NonNull GuiCustom self() {
        return this;
    }

    @Override
    public GuiCustom copy() {
        return (GuiCustom) super.copy();
    }

    @Override
    protected GuiCustom newGui() {
        return new GuiCustom();
    }
}
