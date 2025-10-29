package me.huanmeng.gui.gui.slot;

import me.huanmeng.gui.gui.AbstractGui;
import me.huanmeng.gui.gui.SlotUtil;
import me.huanmeng.gui.gui.slot.impl.slots.ArraySlots;
import me.huanmeng.gui.gui.slot.impl.slots.ExcludeSlots;
import me.huanmeng.gui.gui.slot.impl.slots.PatternLineSlots;
import me.huanmeng.gui.gui.slot.impl.slots.PatternSlots;
import me.huanmeng.gui.util.MathUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

/**
 * Represents multiple slot positions in a GUI inventory.
 * <p>
 * Slots is a collection interface that defines multiple slot positions in a GUI.
 * It provides various factory methods for creating slot collections including:
 * <ul>
 *     <li>Direct slot arrays</li>
 *     <li>Pattern-based slot definitions using character grids</li>
 *     <li>Range-based slot selections</li>
 *     <li>Rectangular area selections</li>
 *     <li>Exclusion-based selections</li>
 * </ul>
 * <p>
 * Pre-defined patterns include:
 * <ul>
 *     <li>{@link #PATTERN_LINE_DEFAULT} - Standard GUI border pattern</li>
 *     <li>{@link #PATTERN_LINE_PAGE_DEFAULT} - Paginated GUI pattern with bottom controls</li>
 *     <li>{@link #GRID} - Grid layout pattern with borders</li>
 *     <li>{@link #FULL} - All slots in the GUI</li>
 * </ul>
 *
 * @author huanmeng_qwq
 * @since 2023/3/17
 */
@SuppressWarnings("unused")
public interface Slots {

    /**
     * Default pattern for standard GUIs with borders.
     * <p>
     * For 2 or fewer rows, fills all slots with 'x'.
     * For 3+ rows, creates a border ('a') around content area ('x'):
     * <pre>
     * aaaaaaaaa
     * axxxxxxxa
     * axxxxxxxa
     * aaaaaaaaa
     * </pre>
     */
    @NonNull
    PatternLineSlots PATTERN_LINE_DEFAULT = Slots.patternLine((line -> {
        List<String> list = new ArrayList<>(line);
        if (line <= 2) {
            list.add("xxxxxxxxx");
            list.add("xxxxxxxxx");
            return list.toArray(new String[0]);
        }
        list.add("aaaaaaaaa");
        for (int i = 0; i < line - 2; i++) {
            list.add("axxxxxxxa");
        }
        list.add("aaaaaaaaa");
        return list.toArray(new String[0]);
    }), 'x');

    /**
     * Default pattern for paginated GUIs with controls at the bottom.
     * <p>
     * For 2 or fewer rows, fills all slots with 'x'.
     * For 3+ rows, creates content area ('x') with bottom control row ('a'):
     * <pre>
     * xxxxxxxxx
     * xxxxxxxxx
     * xxxxxxxxx
     * axxxxxxxa
     * </pre>
     */
    PatternLineSlots PATTERN_LINE_PAGE_DEFAULT = Slots.patternLine(line -> {
        List<String> list = new ArrayList<>(line);
        if (line <= 2) {
            list.add("xxxxxxxxx");
            list.add("xxxxxxxxx");
            return list.toArray(new String[0]);
        }
        for (int i = 0; i < line - 1; i++) {
            list.add("xxxxxxxxx");
        }
        list.add("axxxxxxxa");
        return list.toArray(new String[0]);
    }, 'x');

    /**
     * Grid pattern with alternating spaces creating a checkerboard effect.
     * <p>
     * For 2 or fewer rows, creates spaced grid:
     * <pre>
     *  x x x x
     *  x x x x
     * </pre>
     * For 3+ rows, adds borders ('a') with spaced content ('x'):
     * <pre>
     * aaaaaaaaa
     *  x x x x
     *  x x x x
     * aaaaaaaaa
     * </pre>
     */
    PatternLineSlots GRID = Slots.patternLine(line -> {
        List<String> list = new ArrayList<>(line);
        if (line <= 2) {
            list.add(" x x x x ");
            list.add(" x x x x ");
            return list.toArray(new String[0]);
        }
        list.add("aaaaaaaaa");
        for (int i = 0; i < line - 2; i++) {
            list.add(" x x x x ");
        }
        list.add("aaaaaaaaa");
        return list.toArray(new String[0]);
    }, 'x');

    /**
     * Represents all slots in a GUI.
     * <p>
     * This Slots instance dynamically generates an array containing every slot
     * in the provided GUI, from index 0 to size-1.
     */
    Slots FULL = new Slots() {

        @Override
        public @NotNull <G extends AbstractGui<@NonNull G>> @NonNull Slot[] slots(@NonNull G gui) {
            int size = gui.size();
            Slot[] slots = new Slot[size];
            for (int i = 0; i < size; i++) {
                slots[i] = Slot.of(i);
            }
            return slots;
        }
    };

    /**
     * Resolves this Slots definition into an array of Slot positions for a specific GUI.
     * <p>
     * This method is called to convert the abstract Slots definition into concrete
     * slot positions based on the GUI's actual size and configuration.
     *
     * @param gui the GUI to resolve slots for
     * @param <G> the GUI type
     * @return array of resolved Slot instances
     */
    @NonNull
    <@NonNull G extends AbstractGui<@NonNull G>> Slot[] slots(@NonNull G gui);

    /**
     * Creates an ArraySlots from individual Slot instances.
     *
     * @param slot variable number of Slot instances
     * @return a new ArraySlots containing the specified slots
     */
    @Contract(value = "_ -> new", pure = true)
    static ArraySlots of(Slot... slot) {
        return new ArraySlots(slot);
    }

    /**
     * Creates an ArraySlots from slot indices.
     *
     * @param slots variable number of slot indices (0-based)
     * @return a new ArraySlots containing slots at the specified indices
     */
    @Contract(value = "_ -> new", pure = true)
    static ArraySlots of(int... slots) {
        return new ArraySlots(Arrays.stream(slots).mapToObj(Slot::of).toArray(Slot[]::new));
    }

    /**
     * Creates slots using a character pattern.
     * <p>
     * Each string in the pattern represents one row of the GUI. Characters in the
     * pattern that match the specified chars will be selected as slots.
     * <p>
     * Example:
     * <pre>
     * Slots.pattern(new String[]{
     *     "xxxxxxxxx",
     *     "x       x",
     *     "xxxxxxxxx"
     * }, 'x');
     * // Selects all 'x' positions, creating a border
     * </pre>
     *
     * @param pattern array of strings representing the GUI layout
     * @param chars characters in the pattern that represent slots to select
     * @return a new PatternSlots matching the specified pattern
     */
    @Contract(value = "!null, _ -> new", pure = true)
    static PatternSlots pattern(String[] pattern, char... chars) {
        return new PatternSlots(pattern, chars);
    }

    /**
     * Creates slots using a dynamic pattern function.
     * <p>
     * The pattern function receives the number of rows in the GUI and returns
     * a pattern array. This allows patterns to adapt to different GUI sizes.
     * <p>
     * Example:
     * <pre>
     * Slots.patternLine(rows -> {
     *     if (rows == 3) return new String[]{"xxxxxxxxx", "xxxxxxxxx", "xxxxxxxxx"};
     *     if (rows == 6) return new String[]{"xxxxxxxxx", ..., "xxxxxxxxx"};
     *     return new String[]{};
     * }, 'x');
     * </pre>
     *
     * @param pattern function that generates pattern based on GUI row count
     * @param chars characters in the pattern that represent slots to select
     * @return a new PatternLineSlots that adapts to GUI size
     */
    @Contract(value = "!null, _ -> new", pure = true)
    static PatternLineSlots patternLine(Function<@NonNull Integer, @NonNull String[]> pattern, char @NonNull ... chars) {
        return new PatternLineSlots(pattern, chars);
    }

    /**
     * Returns the default pattern line slots.
     *
     * @return the {@link #PATTERN_LINE_DEFAULT} pattern
     * @see #PATTERN_LINE_DEFAULT
     */
    @Contract(value = "-> !null", pure = true)
    static PatternLineSlots patternLineDefault() {
        return PATTERN_LINE_DEFAULT;
    }

    /**
     * Creates slots from a range of indices.
     * <p>
     * The range is half-open: [min, max), meaning min is included but max is excluded.
     *
     * @param min the starting index (inclusive)
     * @param max the ending index (exclusive)
     * @return a new ArraySlots containing slots from min to max-1
     */
    @Contract(value = "_, _-> new", pure = true)
    static ArraySlots range(int min, int max) {
        return of(MathUtil.range(min, max));
    }

    /**
     * Creates slots from a rectangular area selection.
     * <p>
     * Selects all slots within the rectangular bounds defined by points a and b
     * (inclusive on all sides). Point a must be the top-left corner (smaller indices)
     * and point b must be the bottom-right corner (larger indices).
     * <p>
     * Example: cut(0, 17) selects a 2-row, 9-column rectangle (slots 0-17).
     *
     * @param a the top-left corner slot index (must be ≤ b)
     * @param b the bottom-right corner slot index
     * @return a new ArraySlots containing all slots in the rectangle
     * @throws IllegalArgumentException if a > b
     */
    static ArraySlots cut(int a, int b) {
        if (a > b) {
            throw new IllegalArgumentException("a must be less than or equal to b");
        }
        int aRow = SlotUtil.getRow(a);
        int aColumn = SlotUtil.getColumn(a);
        int bRow = SlotUtil.getRow(b);
        int bColumn = SlotUtil.getColumn(b);
        return of(MathUtil.cut(aRow, bRow, aColumn, bColumn));
    }

    /**
     * Creates player inventory slots from a rectangular area selection.
     * <p>
     * Similar to {@link #cut(int, int)} but for player inventory slots.
     * Player inventories have slots 0-35: hotbar (0-8), main inventory rows (9-35).
     * <p>
     * The method automatically handles the Minecraft inventory slot mapping where
     * the visual bottom row (hotbar) is actually slots 0-8, and the visual top
     * three rows are slots 9-35.
     *
     * @param a the top-left corner slot index (must be ≥ 0, ≤ 35, and ≤ b)
     * @param b the bottom-right corner slot index (must be ≥ 0, ≤ 35)
     * @return a new PlayerSlots containing all player inventory slots in the rectangle
     * @throws IllegalArgumentException if constraints are violated
     */
    static PlayerSlots cutPlayer(@Range(from = 0, to = 35) int a, @Range(from = 0, to = 35) int b) {
        if (a > b) {
            throw new IllegalArgumentException("a must be less than or equal to b");
        }
        if (a < 0) {
            throw new IllegalArgumentException("a must be greater than or equal to 0");
        }
        if (b > 35) {
            throw new IllegalArgumentException("b must be less than or equal to 35");
        }
        int aRow = SlotUtil.getRow(a);
        int aColumn = SlotUtil.getColumn(a);
        int bRow = SlotUtil.getRow(b);
        int bColumn = SlotUtil.getColumn(b);
        if (aRow > 4 || bRow > 4) {
            throw new IllegalArgumentException("aRow and bRow must be less than 5");
        }
        int[] slots = MathUtil.cut(aRow, bRow, aColumn, bColumn);
        int[] playerSlots = new int[slots.length];
        for (int i = 0; i < slots.length; i++) {
            if (slots[i] >= 27) {
                playerSlots[i] = slots[i] - 27;
            } else {
                playerSlots[i] = slots[i] + 9;
            }
        }
        return of(playerSlots).asPlayer();
    }

    /**
     * Creates slots that exclude specific indices from the full slot set.
     * <p>
     * This is useful when you want "all slots except these specific ones."
     *
     * @param slots the slot indices to exclude
     * @return a new ExcludeSlots containing all GUI slots except the specified ones
     */
    @Contract(value = "_-> new", pure = true)
    static ExcludeSlots exclude(int... slots) {
        return new ExcludeSlots(full(), slots);
    }

    /**
     * Creates slots that exclude positions matching a pattern from the full slot set.
     * <p>
     * This is useful when you want "all slots except those matching this pattern."
     *
     * @param pattern array of strings representing the GUI layout
     * @param chars characters in the pattern that represent slots to exclude
     * @return a new ExcludeSlots containing all GUI slots except pattern matches
     */
    @Contract(value = "_, _-> new", pure = true)
    static ExcludeSlots excludePattern(String[] pattern, char... chars) {
        return new ExcludeSlots(full(), pattern(pattern, chars));
    }

    /**
     * Creates slots that exclude a range of indices from the full slot set.
     * <p>
     * The range is half-open: [min, max), meaning min is included but max is excluded.
     *
     * @param min the starting index to exclude (inclusive)
     * @param max the ending index to exclude (exclusive)
     * @return a new ExcludeSlots containing all GUI slots except the range
     */
    @Contract(value = "_, _-> new", pure = true)
    static ExcludeSlots excludeRange(int min, int max) {
        return new ExcludeSlots(full(), range(min, max));
    }

    /**
     * Returns a Slots instance representing all positions in the GUI.
     *
     * @return the {@link #FULL} slots instance
     * @see #FULL
     */
    @Contract(value = "-> !null", pure = true)
    static Slots full() {
        return FULL;
    }

    /**
     * Converts this Slots to PlayerSlots.
     * <p>
     * This wraps the current Slots to indicate they reference the player's inventory
     * rather than the GUI inventory.
     *
     * @return a PlayerSlots wrapping this Slots
     */
    default PlayerSlots asPlayer() {
        return new PlayerSlots(this);
    }
}
