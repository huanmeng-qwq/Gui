package me.huanmeng.opensource.bukkit.gui.slot.impl.slots;

import me.huanmeng.opensource.bukkit.gui.AbstractGui;
import me.huanmeng.opensource.bukkit.gui.slot.Slot;
import me.huanmeng.opensource.bukkit.gui.slot.Slots;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 2022/7/1<br>
 * VioLeaft<br>
 *
 * @author huanmeng_qwq
 */
public class ExcludeSlots implements Slots {
    private Set<Integer> excludeSlots;
    private Slots slots;

    public ExcludeSlots(int[] excludeSlots) {
        this.excludeSlots = new HashSet<>(excludeSlots.length);
        this.excludeSlots.addAll(Arrays.stream(excludeSlots).boxed().collect(Collectors.toList()));
    }

    public ExcludeSlots(Slots slots) {
        this.slots = slots;
        this.excludeSlots = new HashSet<>(10);
    }

    @Override
    public <G extends AbstractGui<G>> Slot[] slots(G gui) {
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
