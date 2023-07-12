package me.huanmeng.opensource.bukkit.gui;

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
import me.huanmeng.opensource.bukkit.util.item.ItemUtil;
import me.huanmeng.opensource.scheduler.Scheduler;
import me.huanmeng.opensource.scheduler.Schedulers;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

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
public abstract class AbstractGui<@NonNull G extends AbstractGui<@NonNull G>> implements GuiTick {
    @Nullable
    protected Player player;
    @NonNull
    protected Component title = Component.text("Chest");
    //默认优先级
    @NonNull
    protected Set<GuiButton> buttons = new HashSet<>(10);
    //大于默认(buttons)的优先级
    @NonNull
    protected Set<GuiButton> attachedButtons = new HashSet<>(10);
    //内部修改优先级最高
    @NonNull
    protected Set<GuiButton> editButtons = new HashSet<>(10);
    @Nullable
    protected Inventory cacheInventory;
    @Nullable
    protected Function<@NonNull Player, @NonNull AbstractGui<?>> backGuiGetter;
    @Nullable
    protected Runnable backGuiRunner;
    private int size;
    /**
     * 如果点击空白地方是否取消事件
     */
    protected boolean cancelClickOther = true;
    /**
     * 是否取消点击{@link InventoryView}下面部分的{@link Inventory}或切换副手
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
    /**
     * 是否禁用点击事件, 为true时将直接取消{@link InventoryClickEvent}事件并不做任何处理 谨慎使用, 否则正常处理
     */
    protected boolean disableClick = false;

    protected boolean allowReopen = true;
    boolean close = true;
    boolean closing = true;

    @NonNull
    protected List<Consumer<G>> tickles = new ArrayList<>();
    protected int intervalTick = 20 * 5;
    protected boolean tickRefresh = true;
    @Nullable
    protected GuiClick guiClick;
    @Nullable
    protected GuiEmptyItemClick guiEmptyItemClick;
    @Nullable
    protected GuiBottomClick guiBottomClick;
    @Nullable
    protected Consumer<@NonNull G> whenClose;

    protected Scheduler.@Nullable Task tickTask;
    @NonNull
    protected Function<@NonNull HumanEntity, @Nullable Component> errorMessage =
            p -> Component.text("§c无法处理您的点击请求，请联系管理员。");

    /**
     * 设置目标玩家
     */
    public void setPlayer(@NonNull Player player) {
        this.player = player;
    }

    /**
     * 获取目标玩家
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * 设置title与size
     */
    @Deprecated
    protected void init(@NonNull String title, int itemSize) {
        init(Component.text(title), itemSize);
    }

    /**
     * 设置title与size
     */
    protected void init(@NonNull Component title, int itemSize) {
        this.title = title;
        this.size = itemSize;
    }

    /**
     * 打开{@link Inventory}
     */
    @NonNull
    public abstract G openGui();

    /**
     * 创建一个空的{@link Inventory}
     */
    @NonNull
    protected abstract Inventory build(@NonNull InventoryHolder holder);

    void onOpen() {
        close = false;
        closing = false;
        if (schedulerTick() > 0) {
            tickTask = TickManager.tick(this, scheduler(), schedulerTick());
        }
    }

    @NonNull
    public G setTickRefresh(boolean tickRefresh) {
        this.tickRefresh = tickRefresh;
        return self();
    }

    /**
     * 当关闭时, 调用来之{@link InventoryCloseEvent}
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

    public void close(boolean openParent, boolean force) {
        if (!force && (close || closing)) {
            return;
        }
        closing = true;
        if (processingClickEvent) {
            // 在处理点击事件时将close方法延迟到下一tick, 因为下一tick的时候当前的点击事件已经处理完毕
            Schedulers.sync().runLater(() -> {
                processingClickEvent = false;
                close(openParent, force);
            }, 1);
            return;
        }
        if (openParent) {
            if (backGuiGetter != null) {
                AbstractGui<?> gui = backGuiGetter.apply(player);
                if (gui.isAllowReopenFrom(self()) || force) {
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

    /**
     * 返回上一个gui, 没有则关闭
     */
    public void back() {
        back(false);
    }

    /**
     * 返回上一个gui, 没有则关闭
     *
     * @param force 强制返回上一个gui, 即使上一个gui不允许被重新打开
     */
    public void back(boolean force) {
        close(true, force);
    }

    @NonNull
    public G addTick(@NonNull Consumer<G> tickle) {
        tickles.add(tickle);
        return self();
    }

    @NonNull
    public G tick(int tick) {
        this.intervalTick = tick;
        return self();
    }

    @NonNull
    public G errorMessage(@NonNull Function<@NonNull HumanEntity, @Nullable Component> errorMessage) {
        this.errorMessage = errorMessage;
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
    @NonNull
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
    @NonNull
    protected <G extends AbstractGui<G>> boolean isAllowReopenFrom(@NonNull G gui) {
        return allowReopen;
    }

    @NonNull
    protected G precache() {
        GuiManager.instance().userNextOpenGui.put(player.getUniqueId(), this);
        return self();
    }

    @NonNull
    protected G cache(@NonNull Inventory inventory) {
        this.cacheInventory = inventory;
        //noinspection unchecked
        ((GuiHolder<G>) inventory.getHolder()).setInventory(inventory);
        GuiManager.instance().setUserOpenGui(player.getUniqueId(), this);
        return self();
    }


    /**
     * 重新填充所有物品
     */
    @NonNull
    public G refresh(boolean all) {
        fillItems(cacheInventory, all);
        return self();
    }

    @NonNull
    protected G unCache() {
        GuiManager.instance().removeUserOpenGui(player.getUniqueId());
        return self();
    }

    @NonNull
    protected final InventoryHolder createHolder() {
        return new GuiHolder<>(player, self());
    }

    @NonNull
    protected Set<GuiButton> getFillItems() {
        return buttons;
    }

    @NonNull
    public Set<GuiButton> getAttachedItems() {
        return attachedButtons;
    }

    @NonNull
    public G addAttachedButton(@NonNull GuiButton button) {
        attachedButtons.remove(button);
        attachedButtons.add(button);
        return self();
    }

    private void setButton(@NonNull Slot slot, @Nullable Button button) {
        GuiButton guiButton = new GuiButton(slot, button);
        buttons.remove(guiButton);
        attachedButtons.remove(guiButton);
        editButtons.add(guiButton);
    }

    @Nullable
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

    public ItemStack getItem(int index) {
        return cacheInventory.getItem(index);
    }

    public ItemStack getItem(Slot slot) {
        return getItem(slot.getIndex());
    }

    public ItemStack[] getItems(Slots slots) {
        return Arrays.stream(slots.slots(self())).map(this::getItem).toArray(ItemStack[]::new);
    }

    @NonNull
    protected G fillItems(@NonNull Inventory inventory, @Nullable boolean all) {
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

        Set<GuiButton> attachedItems = new HashSet<>(getAttachedItems());
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

    /**
     * 刷新指定的物品
     */
    @NonNull
    public G refresh(@NonNull Slots slots) {
        if (cacheInventory == null) {
            return self();
        }
        for (Slot slot : slots.slots(self())) {
            GuiButton button = getButton(slot.getIndex());
            cacheInventory.setItem(button.getIndex(), button.getButton().getShowItem(player));
        }
        return self();
    }

    protected final boolean check(@NonNull GuiButton guiButton) {
        return guiButton.canPlace(player);
    }

    @NonNull
    public Inventory getInventory() {
        return cacheInventory;
    }

    public int size() {
        return size;
    }

    public boolean allowClick(@NonNull Player player, @NonNull GuiButton button, @NonNull ClickType clickType,
                              @NonNull InventoryAction action, InventoryType.@NonNull SlotType slotType, int slot, int hotBar,
                              @NonNull InventoryClickEvent e) {
        if (guiClick != null) {
            return guiClick.allowClick(player, button, clickType, action, slotType, slot, hotBar, e);
        }
        return true;
    }

    public void onClick(@NonNull InventoryClickEvent e) {
        if (disableClick) {
            e.setCancelled(true);
            processingClickEvent = false;
            return;
        }
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
                Result result = item.onClick(this, player, e.getClick(), e.getAction(), e.getSlotType(), slot, e.getHotbarButton(), e);
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
                        close(true, false);
                        break;
                    }
                }
            } else if (ItemUtil.isAir(e.getCurrentItem())) {
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
            Component message = errorMessage.apply(e.getWhoClicked());
            if (message != null) {
                GuiManager.sendMessage(e.getWhoClicked(), message);
            } else {
                // 如果apply得到的是null?
                e.getWhoClicked().sendMessage("A inventory error occurred, please contact the administrator.");
            }
            // 出现错误, 直接关闭, 不做后续的返回parent之类的处理
            e.getWhoClicked().closeInventory();
            e.setCancelled(true);
        }
        processingClickEvent = false;
    }

    public void onDarg(@NonNull InventoryDragEvent e) {
        // 包装成InventoryClickEvent执行
        for (Map.Entry<Integer, ItemStack> entry : e.getNewItems().entrySet()) {
            // fake event
            InventoryClickEvent inventoryClickEvent = new InventoryClickEvent(e.getView(), InventoryType.SlotType.CONTAINER, entry.getKey(), ClickType.LEFT, InventoryAction.UNKNOWN);
            onClick(inventoryClickEvent);
            if (inventoryClickEvent.isCancelled()) {
                e.setCancelled(true);
            }
        }
    }

    @NonNull
    public GuiDraw<G> draw() {
        return new GuiDraw<>(self());
    }

    @NonNull
    protected abstract G self();

    @NonNull
    public G title(@NonNull String title) {
        return title(Component.text(title));
    }

    @NonNull
    public G title(@NonNull Component title) {
        this.title = title;
        // 已经打开了? 那就先执行close后的操作然后再重新构建一个inventory打开
        if (!close) {
            onClose();
            openGui();
        }
        return self();
    }

    @NonNull
    public G guiClick(@NonNull GuiClick guiClick) {
        this.guiClick = guiClick;
        return self();
    }

    @NonNull
    public G whenClose(@NonNull Consumer<@NonNull G> consumer) {
        this.whenClose = consumer;
        return self();
    }

    @NonNull
    public G backGui(@NonNull Function<@NonNull Player, @NonNull AbstractGui<?>> parentGuiGetter) {
        this.backGuiGetter = parentGuiGetter;
        return self();
    }

    @NonNull
    public G backRunner(@NonNull Runnable runner) {
        backGuiRunner = runner;
        return self();
    }

    @NonNull
    public G guiEmptyItemClick(@NonNull GuiEmptyItemClick guiEmptyItemClick) {
        this.guiEmptyItemClick = guiEmptyItemClick;
        return self();
    }

    public G guiBottomClick(@NonNull GuiBottomClick guiBottomClick) {
        this.guiBottomClick = guiBottomClick;
        return self();
    }

    public G setCancelClickOther(boolean cancelClickOther) {
        this.cancelClickOther = cancelClickOther;
        return self();
    }

    public G setCancelClickBottom(boolean cancelClickBottom) {
        this.cancelClickBottom = cancelClickBottom;
        return self();
    }

    public G setCancelMoveHotBarItemToSelf(boolean cancelMoveHotBarItemToSelf) {
        this.cancelMoveHotBarItemToSelf = cancelMoveHotBarItemToSelf;
        return self();
    }

    public G setCancelMoveItemToSelf(boolean cancelMoveItemToSelf) {
        this.cancelMoveItemToSelf = cancelMoveItemToSelf;
        return self();
    }

    public G setAllowReopen(boolean allowReopen) {
        this.allowReopen = allowReopen;
        return self();
    }

    public void setDisableClick(boolean disableClick) {
        this.disableClick = disableClick;
    }

    public AbstractGui<G> copy() {
        AbstractGui<G> newed = newGui();
        return copy(newed);
    }

    protected abstract AbstractGui<G> newGui();

    protected AbstractGui<G> copy(Object newGui) {
        AbstractGui<G> gui = (AbstractGui<G>) newGui;
        gui.player = player;
        gui.title = title;
        gui.buttons = buttons;
        gui.attachedButtons = attachedButtons;
        gui.editButtons = editButtons;
        gui.backGuiGetter = backGuiGetter;
        gui.backGuiRunner = backGuiRunner;
        gui.cancelClickOther = cancelClickOther;
        gui.cancelClickBottom = cancelClickBottom;
        gui.cancelMoveHotBarItemToSelf = cancelMoveHotBarItemToSelf;
        gui.cancelMoveItemToSelf = cancelMoveItemToSelf;
        gui.disableClick = disableClick;
        gui.tickles = tickles;
        gui.intervalTick = intervalTick;
        gui.tickRefresh = tickRefresh;
        gui.guiClick = guiClick;
        gui.guiEmptyItemClick = guiEmptyItemClick;
        gui.guiBottomClick = guiBottomClick;
        gui.whenClose = whenClose;
        gui.errorMessage = errorMessage;
        gui.allowReopen = allowReopen;
        return gui;
    }
}
