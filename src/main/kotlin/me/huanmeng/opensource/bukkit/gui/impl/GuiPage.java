package me.huanmeng.opensource.bukkit.gui.impl;

import me.huanmeng.opensource.bukkit.gui.GuiButton;
import me.huanmeng.opensource.bukkit.gui.button.Button;
import me.huanmeng.opensource.bukkit.gui.slot.Slot;
import me.huanmeng.opensource.bukkit.gui.slot.Slots;
import me.huanmeng.opensource.page.Pagination;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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

    public int page() {
        return page;
    }

    public int elementsPerPage() {
        return elementsPerPage;
    }

}
