package me.huanmeng.gui.gui.draw;

import me.huanmeng.gui.gui.AbstractGui;
import me.huanmeng.gui.gui.GuiButton;
import me.huanmeng.gui.gui.button.Button;
import me.huanmeng.gui.gui.slot.Slot;
import me.huanmeng.gui.gui.slot.Slots;
import me.huanmeng.gui.util.MathUtil;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

/**
 * Fluent API for drawing buttons onto GUI inventories.
 *
 * <p>GuiDraw provides a convenient builder-style interface for placing buttons
 * at specific slots in a GUI. It supports:
 * <ul>
 *   <li>Setting buttons at individual slots</li>
 *   <li>Filling rectangular areas with buttons</li>
 *   <li>Placing multiple buttons across slot patterns</li>
 *   <li>Method chaining for fluent API usage</li>
 * </ul>
 *
 * <p><b>Usage Example:</b>
 * <pre>{@code
 * GuiCustom gui = new GuiCustom(player);
 * gui.line(3);
 *
 * // Draw buttons using the fluent API
 * gui.draw()
 *     .set(0, 0, backButton)           // Top-left corner
 *     .vertical(1, 0, 1, 8, fillButton) // Fill row 1
 *     .set(Slot.of(13), mainButton)    // Center slot
 *     .set(slots, itemButtons);        // Multiple buttons
 * }</pre>
 *
 * @param <G> the type of AbstractGui being drawn to
 * @author huanmeng_qwq
 * @since 2023/3/17
 */
@SuppressWarnings("unused")
public class GuiDraw<G extends AbstractGui<G>> {
    /**
     * The GUI instance that buttons will be drawn to.
     */
    @NonNull
    private final G gui;

    /**
     * Creates a new GuiDraw for the specified GUI.
     *
     * @param gui the GUI to draw buttons to
     */
    public GuiDraw(@NonNull G gui) {
        this.gui = gui;
    }

    /**
     * Returns the GUI instance this drawer operates on.
     *
     * @return the GUI instance
     */
    @NonNull
    public G gui() {
        return gui;
    }

    /**
     * Draws a button in a rectangular area defined by row and column coordinates.
     *
     * <p>The button will be placed at every slot within the rectangle defined by
     * the corners (row1, column1) and (row2, column2). The order of coordinates
     * doesn't matter - they will be sorted automatically.
     *
     * @param row1 first row coordinate (0-based)
     * @param column1 first column coordinate (0-based)
     * @param row2 second row coordinate (0-based)
     * @param column2 second column coordinate (0-based)
     * @param button the button to place, or null to clear slots
     * @return this GuiDraw for method chaining
     */
    @NonNull
    public GuiDraw<G> vertical(int row1, int column1, int row2, int column2, @Nullable Button button) {
        int[] rowRange = MathUtil.range(row1, row2);
        int[] columnRange = MathUtil.range(column1, column2);
        for (int column : columnRange) {
            for (int row : rowRange) {
                set(row, column, button);
            }
        }
        return self();
    }

    /**
     * Sets a button at a specific row and column position.
     *
     * @param row the row index (0-based)
     * @param column the column index (0-based)
     * @param button the button to place, or null to clear the slot
     * @return this GuiDraw for method chaining
     */
    @NonNull
    public GuiDraw<G> set(int row, int column, @Nullable Button button) {
        set(Slot.ofBukkit(row, column), button);
        return self();
    }

    /**
     * Sets a button at a specific slot index.
     *
     * @param slot the slot index (0-based, sequential)
     * @param button the button to place, or null to clear the slot
     * @return this GuiDraw for method chaining
     */
    @NonNull
    public GuiDraw<G> set(int slot, @Nullable Button button) {
        set(Slot.of(slot), button);
        return self();
    }

    /**
     * Sets the same button at multiple slot positions.
     *
     * <p>Places the specified button at every slot defined by the Slots instance.
     * Useful for filling areas with the same button (e.g., border decorations).
     *
     * @param slots the slots to place the button at
     * @param button the button to place, or null to clear the slots
     * @return this GuiDraw for method chaining
     */
    @NonNull
    public GuiDraw<G> set(@NonNull Slots slots, @Nullable Button button) {
        for (Slot slot : slots.slots(gui)) {
            set(slot, button);
        }
        return self();
    }

    /**
     * Sets multiple buttons at multiple slot positions.
     *
     * <p>Distributes the buttons from the list across the slots defined by the Slots instance.
     * The first button goes to the first slot, the second button to the second slot, etc.
     * If there are more slots than buttons, remaining slots are left unchanged.
     *
     * @param slots the slots to place buttons at
     * @param buttons the list of buttons to distribute across the slots
     * @return this GuiDraw for method chaining
     */
    @NonNull
    public GuiDraw<G> set(@NonNull Slots slots, @NonNull List<Button> buttons) {
        Slot[] slotArray = slots.slots(gui);
        for (int i = 0; i < slotArray.length; i++) {
            if (i >= buttons.size()) {
                break;
            }
            set(slotArray[i], buttons.get(i));
        }
        return self();
    }

    /**
     * Sets a button at a specific Slot.
     *
     * <p>This is the core method that all other set methods delegate to.
     * It adds the button as an "attached button" to the GUI, which has
     * medium priority in the button hierarchy.
     *
     * @param slot the slot to place the button at
     * @param button the button to place, or null to clear the slot
     * @return this GuiDraw for method chaining
     */
    @NonNull
    public GuiDraw<G> set(@NonNull Slot slot, @Nullable Button button) {
        gui.addAttachedButton(new GuiButton(slot, button));
        return self();
    }

    /**
     * Returns this GuiDraw instance for method chaining.
     *
     * @return this instance
     */
    public GuiDraw<G> self() {
        return this;
    }
}
