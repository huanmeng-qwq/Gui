package me.huanmeng.opensource.bukkit.gui.impl;

import me.huanmeng.opensource.bukkit.gui.button.Button;
import me.huanmeng.opensource.bukkit.gui.impl.page.PageArea;
import me.huanmeng.opensource.bukkit.gui.impl.page.PageSetting;
import me.huanmeng.opensource.bukkit.gui.slot.Slots;
import me.huanmeng.opensource.page.Pagination;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
@SuppressWarnings("unused")
public class GuiPage extends AbstractGuiPage<GuiPage> {
    protected PageArea defaultArea;

    public GuiPage(@NonNull Player player, @NonNull List<? extends Button> allItems, @NonNull Slots elementSlots) {
        this(player, allItems, allItems.size(), elementSlots);
    }

    public GuiPage(@NonNull Player player, @NonNull List<? extends Button> allItems, int elementsPerPage, @NonNull Slots elementSlots) {
        super(player);
        this.defaultArea = pageArea(elementSlots, allItems, elementsPerPage);
    }

    public GuiPage() {
        super();
        this.defaultArea = pageArea(new PageArea());
    }

    @NonNull
    protected final Pagination<? extends Button> createPagination() {
        this.defaultArea.refreshPagination();
        return this.defaultArea.pagination();
    }

    @NonNull
    public Pagination<? extends Button> pagination() {
        return this.defaultArea.pagination();
    }

    @NonNull
    public GuiPage page(int page) {
        this.defaultArea.currentPage(page);
        return this;
    }

    @NonNull
    public GuiPage pageSetting(@NonNull PageSetting pageSetting) {
        this.defaultArea.pageSetting(pageSetting);
        return this;
    }

    public int page() {
        return this.defaultArea.currentPage();
    }

    public int elementsPerPage() {
        return this.defaultArea.elementsPerPage();
    }

    public void nextPage(int count) {
        this.defaultArea.nextPage(count);
    }

    public void previousPage(int count) {
        this.defaultArea.previousPage(count);
    }

    public void setToFirstPage() {
        this.defaultArea.setToFirstPage();
    }

    public void setToLastPage() {
        this.defaultArea.setToLastPage();
    }

    public boolean hasPreviousPage() {
        return this.defaultArea.hasPreviousPage();
    }

    @NonNull
    public Pagination<? extends Button> getPaginationNotNull() {
        return this.defaultArea.pagination();
    }

    public boolean hasNextPage() {
        return this.defaultArea.hasNextPage();
    }

    @Nullable
    public List<? extends Button> getAllItems() {
        return this.defaultArea.items();
    }

    public void setAllItems(@NonNull List<? extends Button> allItems) {
        this.defaultArea.items(allItems);
    }


    public void setElementsPerPage(int elementsPerPage) {
        this.defaultArea.elementsPerPage(elementsPerPage);
    }

    public int getElementsPerPage() {
        return this.defaultArea.elementsPerPage();
    }

    @Nullable
    public Slots getElementSlots() {
        return this.defaultArea.slots();
    }

    public void setElementSlots(@Nullable Slots elementSlots) {
        this.defaultArea.slots(elementSlots);
    }

    @Nullable
    public PageSetting getPageSetting() {
        return this.defaultArea.pageSetting();
    }

    public PageArea defaultPageArea() {
        return this.defaultArea;
    }

    @NonNull
    @Override
    protected GuiPage self() {
        return this;
    }

    @Override
    public GuiPage copy() {
        return (GuiPage) super.copy();
    }

    @Override
    protected @NonNull GuiPage newGui() {
        return new GuiPage();
    }

    @Override
    protected GuiPage copy(Object newGui) {
        super.copy(newGui);
        return (GuiPage) newGui;
    }
}
