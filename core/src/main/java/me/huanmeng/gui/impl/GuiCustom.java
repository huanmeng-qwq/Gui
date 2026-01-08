package me.huanmeng.gui.impl;

import me.huanmeng.gui.AbstractGui;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A basic custom GUI implementation with fixed slots and buttons.
 * <p>
 * GuiCustom provides a simple chest-style inventory GUI where you can place
 * buttons at specific slots. The GUI size is configurable by setting the number
 * of lines (rows) using {@link #line(int)}.
 *
 * <p>
 * Example usage:
 * <pre>{@code
 * GuiCustom gui = new GuiCustom(player);
 * gui.line(3);  // 3 rows (27 slots)
 * gui.title("My Custom GUI");
 * gui.draw().set(Slot.of(0), Button.of(new ItemStack(Material.DIAMOND)));
 * gui.openGui();
 * }</pre>
 *
 *
 * @author huanmeng_qwq
 * @since 2023/3/17
 * @see AbstractGuiCustom
 * @see AbstractGui
 */
public class GuiCustom extends AbstractGuiCustom<@NonNull GuiCustom> {

    /**
     * Creates a new custom GUI for the specified player.
     *
     * @param player the player who will view this GUI
     */
    public GuiCustom(@NonNull Player player) {
        super(player);
    }

    /**
     * Creates a new custom GUI without a player.
     * <p>
     * The player must be set using {@link #setPlayer(Player)} before opening the GUI.
     * </p>
     */
    public GuiCustom() {
        super();
    }

    /**
     * Returns this instance for method chaining.
     * <p>
     * This method is used internally to support fluent API design with correct generic typing.
     * </p>
     *
     * @return this GuiCustom instance
     */
    @Override
    protected @NonNull GuiCustom self() {
        return this;
    }

    /**
     * Creates a deep copy of this GUI instance.
     * <p>
     * The copied GUI will have the same configuration, buttons, and settings,
     * but will be a separate instance that can be modified independently.
     * </p>
     *
     * @return a new GuiCustom instance with copied properties
     */
    @Override
    public GuiCustom copy() {
        return (GuiCustom) super.copy();
    }

    /**
     * Creates a new empty GUI instance of the same type.
     * <p>
     * This method is used internally by the copy mechanism to create
     * a new instance before copying properties.
     * </p>
     *
     * @return a new empty GuiCustom instance
     */
    @Override
    protected GuiCustom newGui() {
        return new GuiCustom();
    }
}
