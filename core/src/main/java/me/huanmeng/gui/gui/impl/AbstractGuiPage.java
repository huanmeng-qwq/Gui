package me.huanmeng.gui.gui.impl;

import me.huanmeng.gui.gui.GuiButton;
import me.huanmeng.gui.gui.button.Button;
import me.huanmeng.gui.gui.impl.page.PageArea;
import me.huanmeng.gui.gui.impl.page.PageButton;
import me.huanmeng.gui.gui.impl.page.PageSetting;
import me.huanmeng.gui.gui.slot.Slot;
import me.huanmeng.gui.gui.slot.Slots;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * 2024/12/28<br>
 * Bukkit-Gui-pom<br>
 *
 * @author huanmeng_qwq
 */
public abstract class AbstractGuiPage<G extends AbstractGuiPage<@NonNull G>> extends AbstractGuiCustom<G> {
    protected List<PageArea> pageAreas = new ArrayList<>();

    public AbstractGuiPage(@NonNull Player player) {
        super(player);
    }

    public AbstractGuiPage() {
        super();
    }

    public PageArea pageArea(Slots slots, List<Button> items, int elementsPerPage) {
        return pageArea(new PageArea().slots(slots).items(items).elementsPerPage(elementsPerPage));
    }

    public PageArea pageArea(PageArea pageArea) {
        this.pageAreas.add(pageArea);
        return pageArea;
    }

    @Override
    @NonNull
    protected Set<GuiButton> getFillItems() {
        if (player == null) {
            throw new IllegalArgumentException("player is null");
        }
        buttons.clear();
        Set<GuiButton> buttons = new HashSet<>();
        for (PageArea pageArea : pageAreas) {
            refresh(pageArea.slots());
            List<Button> buttonList = pageArea.getCurrentItems();
            ArrayList<Slot> slots = new ArrayList<>(Arrays.asList(Objects.requireNonNull(pageArea.slots()).slots(self())));
            for (Button button : buttonList) {
                if (slots.isEmpty()) {
                    break;
                }
                Slot slot = slots.remove(0);
                buttons.add(new GuiButton(slot, button));
            }
            PageSetting pageSetting = pageArea.pageSetting();
            if (pageSetting != null) {
                for (Supplier<PageButton> supplier : pageSetting.pageButtons()) {
                    PageButton pageButton = supplier.get();
                    Slot slot = pageButton.slot().slot(line, pageArea, this);
                    editButtons.removeIf(button -> button.getSlot().equals(slot));
                    if (pageButton.condition().isAllow(pageArea.currentPage(), pageArea.getMaxPage(), pageArea, pageButton, player)) {
                        editButtons.add(new GuiButton(slot, pageButton));
                    }
                }
            }
        }
        return buttons;
    }

    @Override
    protected G copy(Object newGui) {
        super.copy(newGui);
        G guiPage = (G) newGui;
        // GuiCustom
        guiPage.pageAreas = new ArrayList<>(pageAreas);

        return guiPage;
    }
}
