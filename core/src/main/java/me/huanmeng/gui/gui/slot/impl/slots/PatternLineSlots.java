package me.huanmeng.gui.gui.slot.impl.slots;

import com.google.common.primitives.Chars;
import me.huanmeng.gui.gui.AbstractGui;
import me.huanmeng.gui.gui.slot.Slot;
import me.huanmeng.gui.gui.slot.Slots;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
public class PatternLineSlots implements Slots {
    @NonNull
    private final Function<@NonNull Integer, @NonNull String[]> patternFun;
    @NonNull
    private final List<@NonNull Character> chars;

    public PatternLineSlots(@NonNull Function<@NonNull Integer, @NonNull String[]> pattern, char... chars) {
        this.patternFun = pattern;
        this.chars = new ArrayList<>(chars.length);
        this.chars.addAll(Chars.asList(chars));
    }

    @Override
    @NonNull
    public <@NonNull G extends AbstractGui<@NonNull G>> Slot[] slots(@NonNull G gui) {
        String[] pattern = patternFun.apply(gui.size() / 9);
        return applySlots(pattern, this.chars);
    }

    @NonNull
    protected static Slot[] applySlots(@NonNull String[] pattern, @NonNull List<Character> chars2) {
        List<Slot> list = new ArrayList<>(pattern.length);
        for (int row = 0; row < pattern.length; row++) {
            String line = pattern[row];
            char[] chars = line.toCharArray();
            for (int column = 0; column < chars.length; column++) {
                if (chars2.contains(chars[column])) {
                    list.add(Slot.ofBukkit(row, column));
                }
            }
        }
        return list.toArray(new Slot[0]);
    }
    public ArraySlots getSlots(int line) {
        return Slots.of(applySlots(this.patternFun.apply(line), this.chars));
    }

    public ArraySlots getSlots(int line, char... chars) {
        return Slots.of(applySlots(this.patternFun.apply(line), Chars.asList(chars)));
    }

    @NonNull
    public Function<@NonNull Integer, @NonNull String[]> patternFun() {
        return patternFun;
    }
}
