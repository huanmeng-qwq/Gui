package me.huanmeng.opensource.bukkit.gui.impl;

import me.huanmeng.opensource.bukkit.gui.GuiButton;
import me.huanmeng.opensource.bukkit.gui.button.Button;
import me.huanmeng.opensource.bukkit.gui.slot.Slot;
import me.huanmeng.opensource.bukkit.gui.slot.Slots;
import me.huanmeng.opensource.page.Pagination;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * 2022/6/17<br>
 * VioLeaft<br>
 *
 * @author huanmeng_qwq
 */
public class GuiPage extends GuiCustom {
    protected int page = 1;

    protected List<? extends Button> allItems;

    protected int elementsPerPage;

    protected Pagination<? extends Button> pagination;
    protected Slots elementSlots;

    public GuiPage(Player player, List<? extends Button> allItems, Slots elementSlots) {
        this(player, allItems, allItems.size(), elementSlots);
    }

    public GuiPage(Player player, List<? extends Button> allItems, int elementsPerPage, Slots elementSlots) {
        super(player);
        this.allItems = allItems;
        this.elementsPerPage = elementsPerPage;
        this.pagination = createPagination();
        this.elementSlots = elementSlots;
    }

    @Override
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

    @Override
    protected GuiPage self() {
        return this;
    }

    protected final Pagination<? extends Button> createPagination() {
        return new Pagination<>(allItems, elementsPerPage);
    }

    public Pagination<? extends Button> pagination() {
        return pagination;
    }

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
