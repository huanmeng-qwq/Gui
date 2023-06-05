package me.huanmeng.opensource.bukkit.gui.impl;

import me.huanmeng.opensource.bukkit.gui.GuiButton;
import me.huanmeng.opensource.bukkit.gui.button.Button;
import me.huanmeng.opensource.bukkit.gui.impl.page.PageButton;
import me.huanmeng.opensource.bukkit.gui.impl.page.PageSetting;
import me.huanmeng.opensource.bukkit.gui.slot.Slot;
import me.huanmeng.opensource.bukkit.gui.slot.Slots;
import me.huanmeng.opensource.page.Pagination;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;
import java.util.function.Supplier;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
@SuppressWarnings("unused")
public class GuiPage extends GuiCustom {
    protected int page = 1;

    @NonNull
    protected List<? extends Button> allItems;

    protected int elementsPerPage;

    @NonNull
    protected Pagination<? extends Button> pagination;
    @NonNull
    protected Slots elementSlots;

    @Nullable
    protected PageSetting pageSetting;

    public GuiPage(@NonNull Player player, @NonNull List<? extends Button> allItems, @NonNull Slots elementSlots) {
        this(player, allItems, allItems.size(), elementSlots);
    }

    public GuiPage(@NonNull Player player, @NonNull List<? extends Button> allItems, int elementsPerPage, @NonNull Slots elementSlots) {
        super(player);
        this.allItems = allItems;
        this.elementsPerPage = elementsPerPage;
        this.pagination = createPagination();
        this.elementSlots = elementSlots;
    }

    @Override
    @NonNull
    protected Set<GuiButton> getFillItems() {
        buttons.clear();
        Set<GuiButton> buttons = new HashSet<>();
        List<? extends Button> buttonList = pagination.getElementsFor(page);
        ArrayList<Slot> slots = new ArrayList<>(Arrays.asList(this.elementSlots.slots(self())));
        for (Button button : buttonList) {
            if (slots.isEmpty()) {
                break;
            }
            Slot slot = slots.remove(0);
            buttons.add(new GuiButton(slot, button));
        }
        if (pageSetting != null) {
            for (Supplier<PageButton> supplier : pageSetting.pageButtons()) {
                PageButton pageButton = supplier.get();
                Slot slot = pageButton.slot().apply(line);
                editButtons.removeIf(button -> button.getSlot().equals(slot));
                if (pageButton.condition().isAllow(page, pagination.getMaxPage(), this, pageButton, player)) {
                    editButtons.add(new GuiButton(slot, pageButton));
                }
            }
        }
        return buttons;
    }

    @NonNull
    @Override
    protected GuiPage self() {
        return this;
    }

    @NonNull
    protected final Pagination<? extends Button> createPagination() {
        return new Pagination<>(allItems, elementsPerPage);
    }

    @NonNull
    public Pagination<? extends Button> pagination() {
        return pagination;
    }

    @NonNull
    public GuiPage page(int page) {
        this.page = page;
        return this;
    }

    @NonNull
    public GuiPage pageSetting(@NonNull PageSetting pageSetting) {
        this.pageSetting = pageSetting;
        return this;
    }

    public int page() {
        return page;
    }

    public int elementsPerPage() {
        return elementsPerPage;
    }

    public void nextPage(int count) {
        page += count;
    }

    public void previousPage(int count) {
        page -= count;
    }

    public void setToFirstPage() {
        page = 1;
    }

    public void setToLastPage() {
        page = pagination.getMaxPage();
    }

    public boolean hasPreviousPage() {
        return pagination.hasLast(page);
    }

    public boolean hasNextPage() {
        return pagination.hasNext(page);
    }
}
