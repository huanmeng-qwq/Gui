package me.huanmeng.opensource.bukkit.gui.slot.impl.slots;

import com.google.common.primitives.Chars;
import me.huanmeng.opensource.bukkit.gui.AbstractGui;
import me.huanmeng.opensource.bukkit.gui.slot.Slot;
import me.huanmeng.opensource.bukkit.gui.slot.Slots;

import java.util.ArrayList;
import java.util.List;

/**
 * 2022/6/17<br>
 * VioLeaft<br>
 *
 * @author huanmeng_qwq
 */
public class PatternSlots implements Slots {
    private final String[] pattern;
    private final List<Character> chars;

    public PatternSlots(String[] pattern, char... chars) {
        this.pattern = pattern;
        this.chars = new ArrayList<>(chars.length);
        this.chars.addAll(Chars.asList(chars));
    }

    @Override
    public <G extends AbstractGui<G>> Slot[] slots(G gui) {
        List<Slot> list = new ArrayList<>(pattern.length);
        for (int y = 0; y < pattern.length; y++) {
            String line = pattern[y];
            char[] chars = line.toCharArray();
            for (int x = 0; x < chars.length; x++) {
                if (this.chars.contains(chars[x])) {
                    list.add(Slot.ofBukkit(x, y));
                }
            }
        }
        return list.toArray(new Slot[0]);
    }
}
