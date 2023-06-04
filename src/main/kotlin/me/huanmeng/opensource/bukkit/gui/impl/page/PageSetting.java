package me.huanmeng.opensource.bukkit.gui.impl.page;

import com.google.common.collect.ImmutableList;
import me.huanmeng.opensource.bukkit.gui.button.Button;
import me.huanmeng.opensource.bukkit.gui.impl.GuiPage;
import me.huanmeng.opensource.func.FreezeSupplier;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * 2023/6/4<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
@SuppressWarnings("unused")
public class PageSetting {
    /**
     * 上一页按钮
     */
    private List<@NonNull Supplier<@NonNull PageButton>> pageButtons;

    private PageSetting() {
    }

    @Contract(value = " -> new", pure = true)
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * @return 按钮集合
     */
    @NonNull
    public List<@NonNull Supplier<@NonNull PageButton>> pageButtons() {
        return pageButtons;
    }


    public static class Builder {
        /**
         * 按钮集合
         */
        private List<@NonNull Supplier<@NonNull PageButton>> pageButtons;

        /**
         * 添加按钮
         *
         * @param supplier 按钮
         */
        @NonNull
        public Builder button(@NonNull Supplier<@NonNull PageButton> supplier) {
            if (pageButtons == null) {
                pageButtons = new ArrayList<>();
            }
            this.pageButtons.add(supplier);
            return this;
        }

        /**
         * 添加按钮
         *
         * @param gui    gui
         * @param button 按钮
         */
        @NonNull
        public Builder button(@NonNull GuiPage gui, @NonNull Button button) {
            return button(PageButton.of(gui, button));
        }

        /**
         * 添加按钮
         *
         * @param gui             gui
         * @param firstPageButton 按钮
         * @param condition       允许绘制的条件
         */
        @NonNull
        public Builder button(@NonNull GuiPage gui, @NonNull Button firstPageButton, @NonNull PageCondition condition) {
            return button(PageButton.of(gui, firstPageButton, condition));
        }

        /**
         * 添加按钮
         *
         * @param button 按钮
         */
        @NonNull
        public Builder button(@NonNull PageButton button) {
            return this.button(FreezeSupplier.of(button));
        }

        @NonNull
        public PageSetting build() {
            PageSetting pageSetting = new PageSetting();
            pageSetting.pageButtons = ImmutableList.copyOf(pageButtons);
            return pageSetting;
        }
    }
}
