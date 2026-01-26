package me.huanmeng.gui.slot.impl.slot;

import me.huanmeng.gui.button.Button;
import me.huanmeng.gui.button.ClickData;
import me.huanmeng.gui.enums.Result;
import me.huanmeng.gui.slot.function.ButtonClickInterface;
import me.huanmeng.gui.slot.function.ButtonPlaceInterface;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 * @since 1.0
 */
@SuppressWarnings("unused")
public class SlotInterface extends SlotImpl {
    @NonNull
    protected final ButtonClickInterface clickInterface;
    @NonNull
    protected final ButtonPlaceInterface buttonPlaceInterface;

    public SlotInterface(int index, @NonNull ButtonClickInterface clickInterface, @NonNull ButtonPlaceInterface buttonPlaceInterface) {
        super(index);
        this.clickInterface = clickInterface;
        this.buttonPlaceInterface = buttonPlaceInterface;
    }

    public SlotInterface(int index, @NonNull ButtonClickInterface clickInterface) {
        super(index);
        this.clickInterface = clickInterface;
        this.buttonPlaceInterface = ButtonPlaceInterface.ALWAYS_TRUE;
    }

    public SlotInterface(int index, @NonNull ButtonPlaceInterface buttonPlaceInterface) {
        super(index);
        this.clickInterface = (clickData) -> {
            Button button = clickData.button;
            if (button != null) {
                return button.handleClick(clickData);
            }
            return Result.CANCEL;
        };
        this.buttonPlaceInterface = buttonPlaceInterface;
    }

    @Override
    public @NonNull Result handleClick(@NonNull ClickData clickData) {
        return clickInterface.handleClick(clickData);
    }

    @Override
    public boolean canPlace(@NonNull Button button, @NonNull Player player) {
        return buttonPlaceInterface.canPlace(this, button, player);
    }
}
