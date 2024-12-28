package me.huanmeng.opensource.bukkit.gui.slot;

import me.huanmeng.opensource.bukkit.gui.AbstractGui;
import me.huanmeng.opensource.bukkit.gui.slot.impl.slots.ArraySlots;
import me.huanmeng.opensource.bukkit.gui.slot.impl.slots.ExcludeSlots;
import me.huanmeng.opensource.bukkit.gui.slot.impl.slots.PatternLineSlots;
import me.huanmeng.opensource.bukkit.gui.slot.impl.slots.PatternSlots;
import me.huanmeng.opensource.bukkit.util.MathUtil;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
@SuppressWarnings("unused")
public interface Slots {
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

    @NonNull
    <@NonNull G extends AbstractGui<@NonNull G>> Slot[] slots(@NonNull G gui);

    @Contract(value = "_ -> new", pure = true)
    static ArraySlots of(Slot... slot) {
        return new ArraySlots(slot);
    }

    @Contract(value = "_ -> new", pure = true)
    static ArraySlots of(int... slots) {
        return new ArraySlots(Arrays.stream(slots).mapToObj(Slot::of).toArray(Slot[]::new));
    }

    /**
     * 用图案表达位置
     */
    @Contract(value = "!null, _ -> new", pure = true)
    static PatternSlots pattern(String[] pattern, char... chars) {
        return new PatternSlots(pattern, chars);
    }

    /**
     * 用图案表达位置
     */
    @Contract(value = "!null, _ -> new", pure = true)
    static PatternLineSlots patternLine(Function<@NonNull Integer, @NonNull String[]> pattern, char @NonNull ... chars) {
        return new PatternLineSlots(pattern, chars);
    }

    @Contract(value = "-> !null", pure = true)
    static PatternLineSlots patternLineDefault() {
        return PATTERN_LINE_DEFAULT;
    }

    /**
     *
     * @param min 包含
     * @param max 不包含
     * @return
     */
    @Contract(value = "_, _-> new", pure = true)
    static ArraySlots range(int min, int max) {
        return of(MathUtil.range(min, max));
    }

    @Contract(value = "_-> new", pure = true)
    static ExcludeSlots exclude(int... slots) {
        return new ExcludeSlots(full(), slots);
    }

    @Contract(value = "_, _-> new", pure = true)
    static ExcludeSlots excludePattern(String[] pattern, char... chars) {
        return new ExcludeSlots(full(), pattern(pattern, chars));
    }

    @Contract(value = "_, _-> new", pure = true)
    static ExcludeSlots excludeRange(int min, int max) {
        return new ExcludeSlots(full(), range(min, max));
    }

    /**
     * Gui中全部位置
     */
    @Contract(value = "-> !null", pure = true)
    static Slots full() {
        return FULL;
    }
}
