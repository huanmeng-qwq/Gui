package me.huanmeng.opensource.bukkit.gui;

import me.huanmeng.opensource.bukkit.util.item.ItemUtil;
import me.huanmeng.opensource.bukkit.gui.button.Button;
import me.huanmeng.opensource.bukkit.gui.draw.GuiDraw;
import me.huanmeng.opensource.bukkit.gui.enums.Result;
import me.huanmeng.opensource.bukkit.gui.holder.GuiHolder;
import me.huanmeng.opensource.bukkit.gui.interfaces.GuiBottomClick;
import me.huanmeng.opensource.bukkit.gui.interfaces.GuiClick;
import me.huanmeng.opensource.bukkit.gui.interfaces.GuiEmptyItemClick;
import me.huanmeng.opensource.bukkit.gui.interfaces.GuiTick;
import me.huanmeng.opensource.bukkit.gui.slot.Slot;
import me.huanmeng.opensource.bukkit.gui.slot.Slots;
import me.huanmeng.opensource.bukkit.tick.TickManager;
import me.huanmeng.opensource.scheduler.Scheduler;
import me.huanmeng.opensource.scheduler.Schedulers;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
@SuppressWarnings("ALL")
public abstract class AbstractGui<G extends AbstractGui<G>> implements GuiTick {
    protected Player player;
    protected String title = "Chest";
    //默认优先级
    protected Set<GuiButton> buttons = new HashSet<>(10);
    //大于默认(buttons)的优先级
    protected Set<GuiButton> attachedButtons = new HashSet<>(10);
    //内部修改优先级最高
    protected Set<GuiButton> editButtons = new HashSet<>(10);
    protected Inventory cacheInventory;
    protected Function<Player, AbstractGui<?>> backGuiGetter;
    protected Runnable backGuiRunner;
    private int size;
    /**
     * 如果点击空白地方是否取消事件
     */
    protected boolean cancelClickOther = true;
    /**
     * 是否取消点击{@link InventoryView}下面部分的{@link Inventory}
     */
    protected boolean cancelClickBottom = true;
    /**
     * 是否取消玩家用按键将背包物品移动至当前gui
     */
    protected boolean cancelMoveHotBarItemToSelf = true;
    /**
     * 是否取消玩家用鼠标选中拿起的物品移动至当前gui
     *
     * @see #cancelClickBottom
     */
    protected boolean cancelMoveItemToSelf = true;

    protected boolean processingClickEvent;
    boolean close = true;

    protected List<Consumer<G>> tickles = new ArrayList<>();
    protected int intervalTick = 20 * 5;
    protected boolean tickRefresh = true;
    protected GuiClick guiClick;
    protected GuiEmptyItemClick guiEmptyItemClick;
    protected GuiBottomClick guiBottomClick;
    protected Consumer<G> whenClose;

    protected Scheduler.Task tickTask;

    /**
     * 设置目标玩家
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * 设置title与size
     */
    protected void init(String title, int itemSize) {
        this.title = title;
        this.size = itemSize;
    }

    /**
     * 打开{@link Inventory}
     */
    public abstract G openGui();

    /**
     * 创建一个空的{@link Inventory}
     */
    protected abstract Inventory build(InventoryHolder holder);

    void onOpen() {
        close = false;
        if (schedulerTick() > 0) {
            tickTask = TickManager.tick(this, scheduler(), schedulerTick());
        }
    }

    public G setTickRefresh(boolean tickRefresh) {
        this.tickRefresh = tickRefresh;
        return self();
    }

    /**
     * 当关闭时
     */
    public void onClose() {
        close = true;
        if (tickTask != null) {
            tickTask.stop();
        }
        if (whenClose != null) {
            whenClose.accept(self());
        }
    }

    public void close(boolean openParent, boolean ignore) {
        if (processingClickEvent) {
            Schedulers.sync().runLater(() -> {
                processingClickEvent = false;
                close(openParent, ignore);
            }, 1);
            return;
        }
        if (openParent) {
            if (backGuiGetter != null) {
                AbstractGui<?> gui = backGuiGetter.apply(player);
                if (gui.isAllowReopenFrom(self()) || ignore) {
                    gui.setPlayer(player);
                    gui.openGui();
                    return;
                }
            } else if (backGuiRunner != null) {
                backGuiRunner.run();
            } else {
                player.closeInventory();
            }
        } else {
            player.closeInventory();
        }
    }

    public void close() {
        close(false, false);
    }

    public G addTick(Consumer<G> tickle) {
        tickles.add(tickle);
        return self();
    }

    public G tick(int tick) {
        this.intervalTick = tick;
        return self();
    }

    @Override
    public void tick() {
        if (close) {
            return;
        }
        if (processingClickEvent) {
            processingClickEvent = false;
        }
        for (int i = 0; i < tickles.size(); i++) {
            tickles.get(i).accept(self());
        }
        if (tickRefresh) {
            refresh(false);
        }
    }

    @Override
    public Scheduler scheduler() {
        return Schedulers.async();
    }

    @Override
    public int schedulerTick() {
        return intervalTick;
    }

    /**
     * 是否允许返回到当前菜单
     */
    protected <G extends AbstractGui<G>> boolean isAllowReopenFrom(G gui) {
        return true;
    }

    protected G precache() {
        GuiManager.instance().userNextOpenGui.put(player.getUniqueId(), this);
        return self();
    }

    protected G cache(Inventory inventory) {
        this.cacheInventory = inventory;
        //noinspection unchecked
        ((GuiHolder<G>) inventory.getHolder()).setInventory(inventory);
        GuiManager.instance().setUserOpenGui(player.getUniqueId(), this);
        return self();
    }


    /**
     * 刷新{@link Inventory}
     */
    public G refresh(boolean all) {
        fillItems(cacheInventory, all);
        return self();
    }

    protected G unCache() {
        GuiManager.instance().removeUserOpenGui(player.getUniqueId());
        return self();
    }

    protected final InventoryHolder createHolder() {
        return new GuiHolder<>(player, self());
    }

    protected Set<GuiButton> getFillItems() {
        return buttons;
    }

    public Set<GuiButton> getAttachedItems() {
        return attachedButtons;
    }

    public G addAttachedButton(GuiButton button) {
        attachedButtons.remove(button);
        attachedButtons.add(button);
        return self();
    }

    private void setButton(Slot slot, Button button) {
        GuiButton guiButton = new GuiButton(slot, button);
        buttons.remove(guiButton);
        attachedButtons.remove(guiButton);
        editButtons.add(guiButton);
    }

    public GuiButton getButton(int index) {
        return editButtons
                .stream()
                .filter(e -> e.getIndex() == index)
                .findFirst()
                .orElseGet(
                        () -> buttons
                                .stream()
                                .filter(e -> e.getIndex() == index)
                                .findFirst()
                                .orElseGet(
                                        () -> attachedButtons
                                                .stream()
                                                .filter(e -> e.getIndex() == index)
                                                .findFirst()
                                                .orElse(null)
                                )
                );
    }

    protected G fillItems(Inventory inventory, boolean all) {
        if (all) {
            inventory.clear();
        }
        Set<GuiButton> fillItems = getFillItems();
        fillItems.removeAll(editButtons);
        buttons.addAll(fillItems);
        for (GuiButton guiButton : fillItems) {
            if (check(guiButton)) {
                Button button = guiButton.getButton();
                inventory.setItem(guiButton.getIndex(), button.getShowItem(player));
            }
        }

        Set<GuiButton> attachedItems = getAttachedItems();
        attachedItems.removeAll(editButtons);
        for (GuiButton guiButton : attachedItems) {
            if (check(guiButton)) {
                Button button = guiButton.getButton();
                inventory.setItem(guiButton.getIndex(), button.getShowItem(player));
            }
        }

        for (GuiButton guiButton : editButtons) {
            if (check(guiButton)) {
                Button button = guiButton.getButton();
                if (button != null) {
                    inventory.setItem(guiButton.getIndex(), button.getShowItem(player));
                } else {
                    inventory.setItem(guiButton.getIndex(), null);
                }
            }
        }
        return self();
    }

    public G refresh(Slots slots) {
        if (cacheInventory == null) {
            return self();
        }
        for (Slot slot : slots.slots(self())) {
            GuiButton button = getButton(slot.getIndex());
            cacheInventory.setItem(button.getIndex(), button.getButton().getShowItem(player));
        }
        return self();
    }

    protected final boolean check(GuiButton guiButton) {
        return guiButton.canPlace(player);
    }

    public Inventory getInventory() {
        return cacheInventory;
    }

    public int size() {
        return size;
    }

    public boolean allowClick(Player player, GuiButton button, ClickType clickType, InventoryAction action, InventoryType.SlotType slotType, int slot, int hotBar, InventoryClickEvent e) {
        if (guiClick != null) {
            return guiClick.allowClick(player, button, clickType, action, slotType, slot, hotBar, e);
        }
        return true;
    }

    public void onClick(InventoryClickEvent e) {
        processingClickEvent = true;
        Inventory inv = e.getClickedInventory();
        if (inv == null) {
            if (cancelClickOther) {
                e.setCancelled(true);
            }
            return;
        }
        InventoryView view = e.getView();
        if (Objects.equals(e.getClickedInventory(), this.cacheInventory) && Objects.equals(view.getTopInventory(), cacheInventory)) {
            int slot = e.getSlot();
            if (slot == -999) {
                e.setCancelled(true);
                return;
            }
            GuiButton item = getButton(slot);
            if (item != null) {
                if (!allowClick(player, item, e.getClick(), e.getAction(), e.getSlotType(), slot, e.getHotbarButton(), e)) {
                    return;
                }
                Result result = item.onClick(player, e.getClick(), e.getAction(), e.getSlotType(), slot, e.getHotbarButton(), e);
                if (result.isCancel()) {
                    e.setCancelled(true);
                }
                ItemStack itemStack = e.getCurrentItem();
                switch (result) {
                    case ALLOW: {
                        e.setCancelled(false);
                        break;
                    }
                    case CLEAR: {
                        e.setCurrentItem(null);
                        break;
                    }
                    case DECREMENT: {
                        itemStack.setAmount(itemStack.getAmount() - 1);
                        if (itemStack.getAmount() > 0) {
                            e.setCurrentItem(itemStack);
                        } else {
                            e.setCurrentItem(null);
                        }
                        break;
                    }
                    case INCREMENTAL: {
                        itemStack.setAmount(itemStack.getAmount() + 1);
                        e.setCurrentItem(itemStack);
                        break;
                    }
                    case CANCEL_UPDATE: {
                        refresh(Slots.of(slot));
                        break;
                    }
                    case CANCEL_UPDATE_ALL: {
                        refresh(true);
                        break;
                    }
                    case CANCEL_CLOSE: {
                        player.closeInventory();
                        break;
                    }
                }
            } else if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) {
                if (e.getHotbarButton() >= 0 && cancelMoveHotBarItemToSelf && Objects.equals(e.getClickedInventory(), e.getView().getTopInventory())) {
                    e.setCancelled(true);
                }
                if (guiEmptyItemClick != null) {
                    if (guiEmptyItemClick.onClickEmptyButton(player, slot, e.getClick(), e.getAction(), e.getSlotType(), e.getHotbarButton(), e)) {
                        e.setCancelled(true);
                    }
                }
            } else {
                e.setCancelled(true);
            }
            if (!ItemUtil.isAir(e.getCursor()) && cancelMoveItemToSelf) {
                e.setCancelled(true);
            }
        } else if (Objects.equals(view.getBottomInventory(), e.getClickedInventory())) {
            if (cancelClickBottom) {
                e.setCancelled(true);
            }

            if (guiBottomClick != null) {
                if (guiBottomClick.onClickBottom(player, view.getBottomInventory(), e.getClick(), e.getAction(), e.getSlotType(), e.getHotbarButton(), e)) {
                    e.setCancelled(true);
                }
            }
        } else {
            e.getWhoClicked().sendMessage("§c无法处理您的Gui点击请求，请联系管理员。");
            e.getWhoClicked().closeInventory();
            e.setCancelled(true);
        }
    }

    public void onDarg(InventoryDragEvent e) {
        for (Map.Entry<Integer, ItemStack> entry : e.getNewItems().entrySet()) {
            InventoryClickEvent inventoryClickEvent = new InventoryClickEvent(e.getView(), InventoryType.SlotType.CONTAINER, entry.getKey(), ClickType.LEFT, InventoryAction.UNKNOWN);
            onClick(inventoryClickEvent);
            if (inventoryClickEvent.isCancelled()) {
                e.setCancelled(true);
                return;
            }
        }
    }

    public GuiDraw<G> draw() {
        return new GuiDraw<>(self());
    }

    protected abstract G self();

    public G title(String title) {
        this.title = title;
        if (!close) {
            onClose();
            openGui();
        }
        return self();
    }

    public G guiClick(GuiClick guiClick) {
        this.guiClick = guiClick;
        return self();
    }

    public G whenClose(Consumer<G> consumer) {
        this.whenClose = consumer;
        return self();
    }

    public G backGui(Function<Player, AbstractGui<?>> parentGuiGetter) {
        this.backGuiGetter = parentGuiGetter;
        return self();
    }

    public G backRunner(Runnable runner) {
        backGuiRunner = runner;
        return self();
    }

    public AbstractGui<G> guiEmptyItemClick(GuiEmptyItemClick guiEmptyItemClick) {
        this.guiEmptyItemClick = guiEmptyItemClick;
        return this;
    }

    public AbstractGui<G> guiBottomClick(GuiBottomClick guiBottomClick) {
        this.guiBottomClick = guiBottomClick;
        return this;
    }

    public void setCancelClickOther(boolean cancelClickOther) {
        this.cancelClickOther = cancelClickOther;
    }

    public void setCancelClickBottom(boolean cancelClickBottom) {
        this.cancelClickBottom = cancelClickBottom;
    }

    public void setCancelMoveHotBarItemToSelf(boolean cancelMoveHotBarItemToSelf) {
        this.cancelMoveHotBarItemToSelf = cancelMoveHotBarItemToSelf;
    }

    public void setCancelMoveItemToSelf(boolean cancelMoveItemToSelf) {
        this.cancelMoveItemToSelf = cancelMoveItemToSelf;
    }
}
