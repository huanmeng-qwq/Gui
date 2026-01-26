package me.huanmeng.gui.slot.impl.slot;

import me.huanmeng.gui.button.Button;
import me.huanmeng.gui.button.ClickData;
import me.huanmeng.gui.enums.Result;
import me.huanmeng.gui.slot.Slot;
import me.huanmeng.gui.slot.function.ButtonPlaceInterface;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * 2024/12/7<br>
 * Bukkit-Gui-pom<br>
 *
 * @author huanmeng_qwq
 * @since 2.3.0
 */
public class SlotForward implements Slot {
    protected final Slot self;
    protected final Slot forwardSlot;
    @Nullable
    protected final ButtonPlaceInterface buttonPlaceInterface;

    public SlotForward(@NonNull Slot self, @NonNull Slot forwardSlot, @Nullable ButtonPlaceInterface buttonPlaceInterface) {
        this.self = self;
        this.forwardSlot = forwardSlot;
        this.buttonPlaceInterface = buttonPlaceInterface;
    }

    public SlotForward(@NonNull Slot self, @NonNull Slot forwardSlot) {
        this(self, forwardSlot, null);
    }

    @Override
    public int getIndex() {
        return self.getIndex();
    }

    @Override
    public @NonNull Result handleClick(@NonNull ClickData clickData) {
        return Result.forward(forwardSlot);
    }

    @Override
    public boolean canPlace(@NonNull Button button, @NonNull Player player) {
        if (this.buttonPlaceInterface == null) return false;
        return this.buttonPlaceInterface.canPlace(this, button, player);
    }

    @Override
    public boolean isPlayer() {
        return self.isPlayer();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SlotForward that = (SlotForward) o;
        return Objects.equals(self, that.self) && Objects.equals(forwardSlot, that.forwardSlot) && Objects.equals(buttonPlaceInterface, that.buttonPlaceInterface);
    }

    @Override
    public int hashCode() {
        return Objects.hash(self, forwardSlot, buttonPlaceInterface);
    }
}
