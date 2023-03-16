package me.huanmeng.opensource.bukkit.gui.slot;

import me.huanmeng.opensource.bukkit.gui.AbstractGui;
import me.huanmeng.opensource.bukkit.gui.slot.impl.slots.ArraySlots;
import me.huanmeng.opensource.bukkit.gui.slot.impl.slots.ExcludeSlots;
import me.huanmeng.opensource.bukkit.gui.slot.impl.slots.PatternLineSlots;
import me.huanmeng.opensource.bukkit.gui.slot.impl.slots.PatternSlots;
import org.apache.commons.lang.math.IntRange;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public interface Slots {

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

    PatternLineSlots PATTERN_LINE_PAGE_DEFAULT = Slots.patternLine((line -> {
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
    }), 'x');

    <G extends AbstractGui<G>> Slot[] slots(G gui);

    static Slots of(Slot... slot) {
        return new ArraySlots(slot);
    }

    static Slots of(int... slots) {
        return new ArraySlots(Arrays.stream(slots).mapToObj(Slot::of).toArray(Slot[]::new));
    }

    /**
     * 用图案表达位置
     */
    static Slots pattern(String[] pattern, char... chars) {
        return new PatternSlots(pattern, chars);
    }

    /**
     * 用图案表达位置
     */
    static PatternLineSlots patternLine(Function<Integer, String[]> pattern, char... chars) {
        return new PatternLineSlots(pattern, chars);
    }

    static Slots patternLineDefault() {
        return PATTERN_LINE_DEFAULT;
    }

    static Slots range(int min, int max) {
        return of(new IntRange(min, max).toArray());
    }

    static Slots exclude(int... slots) {
        return new ExcludeSlots(slots);
    }

    static Slots excludePattern(String[] pattern, char... chars) {
        return new ExcludeSlots(pattern(pattern, chars));
    }

    static Slots excludeRange(int min, int max) {
        return new ExcludeSlots(range(min, max));
    }
}
