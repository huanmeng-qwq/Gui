package me.huanmeng.opensource.bukkit.gui.impl.page;

import com.google.common.collect.ImmutableSet;
import me.huanmeng.opensource.bukkit.gui.button.Button;
import me.huanmeng.opensource.bukkit.gui.button.ClickData;
import me.huanmeng.opensource.bukkit.gui.button.function.page.PlayerClickPageButtonInterface;
import me.huanmeng.opensource.bukkit.gui.enums.Result;
import me.huanmeng.opensource.bukkit.gui.impl.AbstractGuiPage;
import me.huanmeng.opensource.bukkit.gui.impl.GuiPage;
import me.huanmeng.opensource.bukkit.gui.slot.Slot;
import me.huanmeng.opensource.bukkit.util.item.ItemUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 2023/6/4<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
@SuppressWarnings("unused")
public class PageButton implements Button {
    private AbstractGuiPage<?> gui;
    private PageArea pageArea;
    @Nullable
    private Button origin;
    @Nullable
    private PageCondition condition;
    @Nullable
    private PlayerClickPageButtonInterface playerClickPageButtonInterface;
    private Set<@NonNull PageButtonType> types;
    /**
     * 通过行数获取对应的槽位
     */
    @NonNull
    private PageSlot slot = (line, area, gui) -> {
        if (types != null) {
            return types.stream().findFirst().map(type -> type.recommendSlot(line)).orElse(Slot.ofGame(line, 1));
        }
        return Slot.ofGame(line, 1);
    };

    private PageButton() {
    }

    /**
     * @return 获取Builder
     */
    @NonNull
    public Builder toBuilder() {
        return new Builder(gui, pageArea)
                .button(origin)
                .condition(condition)
                .click(playerClickPageButtonInterface)
                .types(types.toArray(new PageButtonType[0]))
                .slot(slot)
                ;
    }

    /**
     * 构建器
     *
     * @param gui gui
     * @return {@link Builder}
     */
    @NonNull
    public static Builder builder(@NonNull GuiPage gui) {
        return new Builder(gui);
    }

    /**
     * 构建器
     *
     * @param gui gui
     * @return {@link Builder}
     */
    @NonNull
    public static Builder builder(@NonNull AbstractGuiPage<?> gui, PageArea area) {
        return new Builder(gui, area);
    }

    /**
     * 构建器
     *
     * @param gui    gui
     * @param button 按钮
     * @return {@link Builder}
     */
    @NonNull
    public static PageButton of(@NonNull GuiPage gui, @NonNull Button button) {
        return PageButton.builder(gui).button(button).build();
    }

    @NonNull
    public static PageButton of(@NonNull AbstractGuiPage<?> gui, @NonNull PageArea pageArea, @NonNull Button button) {
        return PageButton.builder(gui, pageArea).button(button).build();
    }

    /**
     * 构建器
     *
     * @param gui       gui
     * @param button    按钮
     * @param condition 允许绘制的条件
     * @return {@link Builder}
     */
    @NonNull
    public static PageButton of(@NonNull AbstractGuiPage<?> gui, @NonNull PageArea pageArea, @NonNull Button button, @NonNull PageCondition condition) {
        return PageButton.builder(gui, pageArea).button(button).condition(condition).build();
    }

    @NonNull
    public static PageButton of(@NonNull GuiPage gui, @NonNull Button button, @NonNull PageCondition condition) {
        return of(gui, gui.defaultPageArea(), button, condition);
    }

    /**
     * 构建器
     *
     * @param gui       gui
     * @param button    按钮
     * @param condition 允许绘制的条件
     * @param click     点击事件
     * @param types     按钮类型
     * @return {@link Builder}
     */
    @NonNull
    public static PageButton of(@NonNull AbstractGuiPage<?> gui, @NonNull PageArea pageArea, @NonNull Button button, @NonNull PageCondition condition, @NonNull PlayerClickPageButtonInterface click, @NonNull PageButtonType... types) {
        return PageButton.builder(gui, pageArea)
                .button(button)
                .condition(condition)
                .click(click)
                .types(types).build();
    }

    @NonNull
    public static PageButton of(@NonNull GuiPage gui, @NonNull Button button, @NonNull PageCondition condition, @NonNull PlayerClickPageButtonInterface click, @NonNull PageButtonType... types) {
        return of(gui, gui.defaultPageArea(), button, condition, click, types);
    }

    /**
     * 构建器
     *
     * @param gui       gui
     * @param button    按钮
     * @param condition 允许绘制的条件
     * @param click     点击事件
     * @param slot      通过行数获取对应的槽位
     * @param types     按钮类型
     * @return {@link Builder}
     */
    @NonNull
    public static PageButton of(@NonNull AbstractGuiPage<?> gui, @NonNull PageArea pageArea, @NonNull Button button, @NonNull PageCondition condition,
                                @NonNull PlayerClickPageButtonInterface click,
                                @Nullable PageSlot slot, @NonNull PageButtonType... types) {
        return PageButton.builder(gui, pageArea)
                .button(button)
                .condition(condition)
                .click(click)
                .types(types)
                .slot(slot).build();
    }

    @NonNull
    public static PageButton of(@NonNull GuiPage gui, @NonNull Button button, @NonNull PageCondition condition,
                                @NonNull PlayerClickPageButtonInterface click,
                                @Nullable PageSlot slot, @NonNull PageButtonType... types) {
        return of(gui, gui.defaultPageArea(), button, condition, click, slot, types);
    }

    /**
     * @return 原始按钮
     */
    @Nullable
    public Button origin() {
        return origin;
    }

    /**
     * @return 允许绘制的条件
     */
    @NonNull
    public PageCondition condition() {
        return condition != null ? condition : PageCondition.dummy();
    }

    /**
     * @return 通过行数获取对应的槽位
     */
    @NonNull
    public PageSlot slot() {
        return slot;
    }

    @Override
    public @Nullable ItemStack getShowItem(@NonNull Player player) {
        if (origin == null) return null;
        if (condition != null) {
            if (!condition.isAllow(pageArea.currentPage(), pageArea.getMaxPage(), pageArea, this, player)) {
                return null;
            }
        }
        return origin.getShowItem(player);
    }

    @Override
    public @NonNull Result onClick(@NonNull ClickData clickData) {
        InventoryClickEvent e = clickData.event;
        if (ItemUtil.isAir(e.getCurrentItem())) {
            return Result.CANCEL;
        }
        if (origin == null) {
            return Result.CANCEL;
        }
        Result result = origin.onClick(clickData);
        if (playerClickPageButtonInterface == null || types.isEmpty()) {
            return result;
        }
        Iterator<PageButtonType> iterator = types.iterator();
        PageButtonType buttonType = iterator.next();
        if (iterator.hasNext()) {
            while (iterator.hasNext()) {
                PageButtonType type = iterator.next();
                if (type.mainType() == buttonType.mainType() || type.subType() == clickData.click) {
                    buttonType = type;
                    break;
                }
            }
        }
        if (!buttonType.hasPage(pageArea)) {
            return Result.CANCEL;
        }
        return playerClickPageButtonInterface.onClick(gui, pageArea, buttonType, clickData);
    }

    /**
     * @return 按钮类型
     */
    @NonNull
    public Set<@NonNull PageButtonType> types() {
        return types;
    }

    public static class Builder {
        private final AbstractGuiPage<?> gui;
        private final PageArea pageArea;
        private PageSlot slot;
        private Button origin;
        private PageCondition condition;
        private PlayerClickPageButtonInterface playerClickPageButtonInterface;
        private final Set<@NonNull PageButtonType> types = new LinkedHashSet<>(PageButtonTypes.values().length);

        public Builder(@NonNull GuiPage gui) {
            this.gui = gui;
            this.pageArea = gui.defaultPageArea();
        }

        public Builder(@NonNull AbstractGuiPage<?> gui, PageArea pageArea) {
            this.gui = gui;
            this.pageArea = pageArea;
        }

        /**
         * 原始按钮
         */
        @NonNull
        public Builder button(Button origin) {
            this.origin = origin;
            return this;
        }

        /**
         * 允许绘制的条件
         */
        @NonNull
        public Builder condition(PageCondition condition) {
            this.condition = condition;
            return this;
        }

        /**
         * 点击事件
         */
        @NonNull
        public Builder click(PlayerClickPageButtonInterface playerClickPageButtonInterface) {
            this.playerClickPageButtonInterface = playerClickPageButtonInterface;
            return this;
        }

        /**
         * 按钮类型
         */
        @NonNull
        public Builder types(@NonNull PageButtonType... types) {
            this.types.addAll(Arrays.asList(types));
            return this;
        }

        /**
         * 通过行数获取对应的槽位
         */
        @NonNull
        public Builder slot(@Nullable PageSlot slot) {
            this.slot = slot;
            return this;
        }

        /**
         * 构建
         */
        @NonNull
        public PageButton build() {
            PageButton pageButton = new PageButton();
            pageButton.gui = this.gui;
            pageButton.pageArea = this.pageArea;
            pageButton.origin = this.origin;
            pageButton.condition = this.condition;
            pageButton.playerClickPageButtonInterface = this.playerClickPageButtonInterface;
            pageButton.types = ImmutableSet.copyOf(this.types);
            if (this.slot != null) {
                pageButton.slot = this.slot;
            }
            return pageButton;
        }
    }
}
