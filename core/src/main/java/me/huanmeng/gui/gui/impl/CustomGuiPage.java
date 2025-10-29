package me.huanmeng.gui.gui.impl;

import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * An advanced paginated GUI implementation that supports multiple independent page areas.
 * <p>
 * CustomGuiPage extends {@link AbstractGuiPage} to provide more flexibility than {@link GuiPage}.
 * Unlike GuiPage which has a single default page area, CustomGuiPage allows you to create
 * and manage multiple {@link me.huanmeng.gui.gui.impl.page.PageArea}s, each with its own set of items, slots, and pagination
 * settings.
 *
 * <p>
 * This is useful for complex GUIs that need to display multiple independent paginated sections
 * simultaneously. For example, you might have one page area for items and another for enchantments,
 * each with their own navigation controls.
 *
 * <p>
 * Example usage:
 * <pre>{@code
 * CustomGuiPage gui = new CustomGuiPage(player);
 *
 * // Create first page area for items
 * PageArea itemArea = new PageArea()
 *     .slots(Slots.ofPattern("OOOOOOOOO", "OOOOOOOOO"))
 *     .items(itemList)
 *     .elementsPerPage(18)
 *     .pageSetting(PageSettings.normal(gui));
 * gui.pageArea(itemArea);
 *
 * // Create second page area for something else
 * PageArea otherArea = new PageArea()
 *     .slots(Slots.ofPattern("XXXXXXXXX"))
 *     .items(otherList)
 *     .elementsPerPage(9);
 * gui.pageArea(otherArea);
 *
 * gui.openGui();
 * }</pre>
 *
 *
 * @author huanmeng_qwq
 * @since 2024/12/28
 * @see AbstractGuiPage
 * @see GuiPage
 * @see me.huanmeng.gui.gui.impl.page.PageArea
 */
public class CustomGuiPage extends AbstractGuiPage<CustomGuiPage>{

    /**
     * Creates a new custom paginated GUI for the specified player.
     *
     * @param player the player who will view this GUI
     */
    public CustomGuiPage(@NonNull Player player) {
        super(player);
    }

    /**
     * Creates a new custom paginated GUI without a player.
     * <p>
     * The player must be set using {@link #setPlayer(Player)} before opening the GUI.
     * </p>
     */
    public CustomGuiPage() {
        super();
    }

    /**
     * Returns this instance for method chaining.
     * <p>
     * This method is used internally to support fluent API design with correct generic typing.
     * </p>
     *
     * @return this CustomGuiPage instance
     */
    @Override
    protected @NonNull CustomGuiPage self() {
        return this;
    }

    /**
     * Creates a new empty GUI instance of the same type.
     * <p>
     * This method is used internally by the copy mechanism to create
     * a new instance before copying properties.
     * </p>
     *
     * @return a new empty CustomGuiPage instance
     */
    @Override
    protected @NonNull CustomGuiPage newGui() {
        return new CustomGuiPage();
    }

    /**
     * Creates a deep copy of this GUI instance.
     * <p>
     * The copied GUI will have the same configuration, buttons, page areas, and settings,
     * but will be a separate instance that can be modified independently.
     * </p>
     *
     * @return a new CustomGuiPage instance with copied properties
     */
    @Override
    public CustomGuiPage copy() {
        return (CustomGuiPage) super.copy();
    }
}
