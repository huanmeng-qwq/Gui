package me.huanmeng.opensource.bukkit.gui.impl.page;

import me.huanmeng.opensource.bukkit.gui.impl.GuiPage;
import me.huanmeng.opensource.bukkit.gui.slot.Slot;
import org.bukkit.event.inventory.ClickType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

public enum PageButtonTypes implements PageButtonType {
    /**
     * 上一页
     */
    PREVIOUS(ClickType.LEFT, null) {
        @Override
        public void changePage(@NonNull PageArea area) {
            area.previousPage(1);
        }

        @Override
        public boolean hasPage(@NonNull PageArea area) {
            return area.hasPreviousPage();
        }

        @NotNull
        @Override
        public Slot recommendSlot(int line) {
            return Slot.ofGame(1, line);
        }
    },
    /**
     * 下一页
     */
    NEXT(ClickType.LEFT, ClickType.RIGHT) {
        @Override
        public void changePage(@NonNull PageArea area) {
            area.nextPage(1);
        }

        @Override
        public boolean hasPage(@NonNull PageArea area) {
            return area.hasNextPage();
        }

        @NotNull
        @Override
        public Slot recommendSlot(int line) {
            return Slot.ofGame(9, line);
        }
    },
    /**
     * 首页, 第一页
     */
    FIRST(ClickType.LEFT, ClickType.SHIFT_LEFT) {
        @Override
        public void changePage(@NonNull PageArea area) {
            area.setToFirstPage();
        }

        @Override
        public boolean hasPage(@NonNull PageArea area) {
            return area.currentPage() > area.pagination().getMinPage();
        }

        @NotNull
        @Override
        public Slot recommendSlot(int line) {
            return Slot.ofGame(1, line);
        }
    },
    /**
     * 尾页
     */
    LAST(ClickType.LEFT, ClickType.SHIFT_RIGHT) {
        @Override
        public void changePage(@NonNull PageArea area) {
            area.setToLastPage();
        }

        @Override
        public boolean hasPage(@NonNull PageArea area) {
            return area.currentPage() < area.getMaxPage();
        }

        @NotNull
        @Override
        public Slot recommendSlot(int line) {
            return Slot.ofGame(9, line);
        }
    },
    ;
    @NonNull
    private final ClickType clickType;
    @Nullable
    private final ClickType subType;

    PageButtonTypes(@NonNull ClickType clickType, @Nullable ClickType subType) {
        this.clickType = clickType;
        this.subType = subType;
    }

    @Override
    @NonNull
    public ClickType mainType() {
        return clickType;
    }

    @Override
    @Nullable
    public ClickType subType() {
        return subType;
    }
}