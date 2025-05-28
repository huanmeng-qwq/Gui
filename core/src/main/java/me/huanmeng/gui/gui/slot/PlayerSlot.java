package me.huanmeng.gui.gui.slot;

import me.huanmeng.gui.gui.button.Button;
import me.huanmeng.gui.gui.button.ClickData;
import me.huanmeng.gui.gui.enums.Result;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * 代表玩家背包中的槽位
 *
 * @author huanmeng_qwq
 */
public class PlayerSlot implements Slot {
    @NonNull
    private final Slot slot;

    public PlayerSlot(@NonNull Slot slot) {
        this.slot = slot;
    }

    @Override
    public int getIndex() {
        return slot.getIndex();
    }

    @Override
    public @NonNull Result onClick(@NonNull ClickData clickData) {
        return this.slot.onClick(new ClickData(clickData.player, clickData.gui, slot, this, clickData.button, clickData.event, clickData.click, clickData.action, clickData.slotType, clickData.slotKey, clickData.hotBarKey));
    }

    @Override
    public boolean tryPlace(@NonNull Button button, @NonNull Player player) {
        return slot.tryPlace(button, player);
    }

    @Override
    public PlayerSlot asPlayer() {
        return this;
    }

    public @NotNull Slot getSlot() {
        return slot;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PlayerSlot that = (PlayerSlot) o;
        return Objects.equals(slot, that.slot);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(slot);
    }
}
