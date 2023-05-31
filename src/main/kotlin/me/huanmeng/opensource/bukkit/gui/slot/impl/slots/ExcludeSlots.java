package me.huanmeng.opensource.bukkit.gui.slot.impl.slots;

import me.huanmeng.opensource.bukkit.gui.AbstractGui;
import me.huanmeng.opensource.bukkit.gui.slot.Slot;
import me.huanmeng.opensource.bukkit.gui.slot.Slots;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
public class ExcludeSlots implements Slots {
    @NonNull
    private final Set<Integer> excludeSlots;
    @Nullable
    private Slots slots;

    public ExcludeSlots(int[] excludeSlots) {
        this.excludeSlots = new HashSet<>(excludeSlots.length);
        this.excludeSlots.addAll(Arrays.stream(excludeSlots).boxed().collect(Collectors.toList()));
    }

    public ExcludeSlots(@NonNull Slots slots) {
        this.slots = slots;
        this.excludeSlots = new HashSet<>(10);
    }

    @Override
    public <@NonNull G extends AbstractGui<@NonNull G>> Slot[] slots(@NotNull G gui) {
        List<Integer> list = null;
        if (slots != null) {
            list = Arrays.stream(slots.slots(gui)).map(Slot::getIndex).collect(Collectors.toList());
            this.excludeSlots.addAll(list);
        }
        List<Slot> slots = new ArrayList<>(gui.size());
        for (int i = 0; i < gui.size(); i++) {
            if (this.excludeSlots.contains(i)) {
                continue;
            }
            slots.add(Slot.of(i));
        }
        if (list != null) {
            list.forEach(this.excludeSlots::remove);
        }
        return slots.toArray(new Slot[0]);
    }
}
