package me.huanmeng.gui.gui.slot.impl.slots;

import me.huanmeng.gui.gui.AbstractGui;
import me.huanmeng.gui.gui.slot.Slot;
import me.huanmeng.gui.gui.slot.Slots;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
public class ExcludeSlots implements Slots {
    @Nullable
    private Set<Integer> excludeList;
    @Nullable
    private Slots excludeSlots;
    @NonNull
    private final Slots slots;

    public ExcludeSlots(@NonNull Slots slots, int[] excludeSlots) {
        this.slots = slots;
        this.excludeList = new HashSet<>(excludeSlots.length);
        this.excludeList.addAll(Arrays.stream(excludeSlots).boxed().collect(Collectors.toList()));
    }

    public ExcludeSlots(@NonNull Slots slots, @Nullable Slots excludeSlots) {
        this.slots = slots;
        this.excludeSlots = excludeSlots;
    }

    @Override
    public <@NonNull G extends AbstractGui<@NonNull G>> Slot[] slots(@NonNull G gui) {
        List<Slot> list = new ArrayList<>();
        List<Integer> dynamicExcludeSlots = new ArrayList<>();
        List<Slot> exclude = new ArrayList<>();
        if (excludeSlots != null) {
            exclude.addAll(Arrays.asList(excludeSlots.slots(gui)));
            for (@NonNull Slot slot : exclude) {
                dynamicExcludeSlots.add(slot.getIndex());
            }
        }
        for (@NonNull Slot slot : slots.slots(gui)) {
            int index = slot.getIndex();
            if (excludeList != null && excludeList.contains(index)) {
                continue;
            }
            if (dynamicExcludeSlots.contains(index) || exclude.contains(slot)) {
                continue;
            }
            list.add(slot);
        }
        return list.toArray(new Slot[0]);
    }
}
