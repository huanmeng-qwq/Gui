package me.huanmeng.opensource.bukkit.gui.slot.impl.slots;

import com.google.common.primitives.Chars;
import me.huanmeng.opensource.bukkit.gui.AbstractGui;
import me.huanmeng.opensource.bukkit.gui.slot.Slot;
import me.huanmeng.opensource.bukkit.gui.slot.Slots;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
public class PatternSlots implements Slots {
    @NonNull
    private final String[] pattern;
    @NonNull
    private final List<@NonNull Character> chars;

    public PatternSlots(@NonNull String[] pattern, char... chars) {
        this.pattern = pattern;
        this.chars = new ArrayList<>(chars.length);
        this.chars.addAll(Chars.asList(chars));
    }

    @Override
    @NonNull
    public <@NonNull G extends AbstractGui<@NonNull G>> Slot[] slots(@NonNull G gui) {
        return PatternLineSlots.applySlots(pattern, this.chars);
    }
}
