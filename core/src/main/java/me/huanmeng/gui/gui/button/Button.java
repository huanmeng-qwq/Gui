package me.huanmeng.gui.gui.button;

import me.huanmeng.gui.gui.button.function.PlayerClickCancelInterface;
import me.huanmeng.gui.gui.button.function.PlayerClickInterface;
import me.huanmeng.gui.gui.button.function.PlayerItemInterface;
import me.huanmeng.gui.gui.button.function.PlayerSimpleCancelInterface;
import me.huanmeng.gui.gui.enums.Result;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
@SuppressWarnings("unused")
@FunctionalInterface
public interface Button {
    /**
     * 获取显示物品
     *
     * @param player 玩家
     * @return 物品
     */
    @Nullable
    ItemStack getShowItem(@NonNull Player player);

    /**
     * 空按钮
     */
    Button EMPTY = (p) -> null;

    @NonNull
    default Result onClick(@NonNull ClickData clickData) {
        return Result.CANCEL;
    }


    /**
     * 展示型按钮
     *
     * @param item 物品
     * @return {@link Button}
     */
    @Contract(value = "!null -> new", pure = true)
    static Button of(PlayerItemInterface item) {
        return new BaseButton(item, null);
    }

    /**
     * 展示型按钮
     *
     * @param item 物品
     */
    @Contract(value = "_ -> new", pure = true)
    static Button of(ItemStack item) {
        return new BaseButton(PlayerItemInterface.of(item), null);
    }

    /**
     * 点击型按钮
     *
     * @param item      物品
     * @param clickable 点击事件
     */
    @Contract(value = "_,_ -> new", pure = true)
    static Button of(ItemStack item, PlayerClickInterface clickable) {
        return new BaseButton(PlayerItemInterface.of(item), clickable);
    }

    /**
     * 点击型按钮
     *
     * @param item      物品
     * @param clickable 点击事件
     * @return {@link Button}
     */
    @Contract(value = "null, null -> new", pure = true)
    static Button of(PlayerItemInterface item, PlayerClickInterface clickable) {
        return new BaseButton(item, clickable);
    }

    /**
     * 点击型按钮
     *
     * @param item      物品
     * @param clickable 点击事件
     * @return {@link Button}
     */
    @ApiStatus.ScheduledForRemoval(inVersion = "2.6.0")
    @Deprecated
    @Contract(value = "null, !null, null -> new", pure = true)
    static <@NonNull T extends PlayerClickInterface> Button of(PlayerItemInterface item, Class<T> cls, T clickable) {
        return new BaseButton(item, clickable);
    }

    @ApiStatus.ScheduledForRemoval(inVersion = "2.6.0")
    @Deprecated
    @Contract(value = "null, !null, null -> new", pure = true)
    static <@NonNull T extends PlayerClickInterface> Button of(ItemStack item, Class<T> cls, T clickable) {
        return new BaseButton(PlayerItemInterface.of(item), clickable);
    }

    /**
     * 点击型按钮
     *
     * @param item      物品
     * @param clickable 点击事件
     * @return {@link Button}
     */
    @ApiStatus.ScheduledForRemoval(inVersion = "2.6.0")
    @Deprecated
    @Contract(value = "null, null -> new", pure = true)
    static Button of(PlayerItemInterface item, PlayerClickCancelInterface clickable) {
        return new BaseButton(item, clickable);
    }

    /**
     * 点击型按钮
     *
     * @param item                        物品
     * @param playerSimpleCancelInterface 点击事件
     * @return {@link Button}
     */
    @ApiStatus.ScheduledForRemoval(inVersion = "2.6.0")
    @Deprecated
    @Contract(value = "_, _ -> new", pure = true)
    static Button of(PlayerItemInterface item, PlayerSimpleCancelInterface playerSimpleCancelInterface) {
        return new BaseButton(item, playerSimpleCancelInterface);
    }

    @ApiStatus.ScheduledForRemoval(inVersion = "2.6.0")
    @Deprecated
    @Contract(value = "_, _ -> new", pure = true)
    static Button of(ItemStack item, PlayerSimpleCancelInterface playerSimpleCancelInterface) {
        return new BaseButton(PlayerItemInterface.of(item), playerSimpleCancelInterface);
    }

    /**
     * 点击型按钮
     *
     * @param impl   实现了{@link PlayerItemInterface}和{@link PlayerClickInterface}的类
     * @param <IMPL> 实现了{@link PlayerItemInterface}和{@link PlayerClickInterface}的类
     * @return {@link Button}
     */
    @Contract(value = "null -> new", pure = true)
    static <@NonNull IMPL extends PlayerItemInterface & PlayerClickInterface> Button ofInstance(IMPL impl) {
        return new BaseButton(impl, impl);
    }

    /**
     * 点击型按钮
     *
     * @param impl   实现了{@link PlayerItemInterface}和{@link PlayerClickInterface}的类
     * @param <IMPL> 实现了{@link PlayerItemInterface}和{@link PlayerClickInterface}的类
     * @return {@link Button}
     */
    @NonNull
    static <@NonNull IMPL extends PlayerItemInterface & PlayerClickInterface> List<Button> ofInstances(@NonNull Collection<@NonNull IMPL> impl) {
        return impl.stream().map(Button::ofInstance).collect(Collectors.toList());
    }

    /**
     * 点击型按钮
     *
     * @param impl   实现了{@link PlayerItemInterface}和{@link PlayerClickInterface}的类
     * @param <IMPL> 实现了{@link PlayerItemInterface}和{@link PlayerClickInterface}的类
     * @return {@link Button}
     */
    @SafeVarargs
    static <@NonNull IMPL extends PlayerItemInterface & PlayerClickInterface> List<Button> ofInstancesArray(@NonNull IMPL... impl) {
        return Arrays.stream(impl).map(Button::ofInstance).collect(Collectors.toList());
    }

    /**
     * 通过映射与物品集合合并按钮
     *
     * @param itemStacks 物品
     * @param map        映射
     * @return {@link Button}
     */
    @NonNull
    static List<@Nullable Button> ofItemStacksButton(@NonNull Collection<@Nullable ItemStack> itemStacks, @NonNull Function<@Nullable ItemStack, @Nullable Button> map) {
        return itemStacks.stream().map(map).collect(Collectors.toList());
    }

    /**
     * 展示型按钮
     *
     * @param itemStacks 物品
     * @param map        映射
     * @return {@link Button}
     */
    @NonNull
    static List<@NonNull Button> ofItemStacks(@NonNull Collection<@Nullable ItemStack> itemStacks, @NonNull Function<@Nullable ItemStack, @NonNull PlayerItemInterface> map) {
        return itemStacks.stream().map(map).map(Button::of).collect(Collectors.toList());
    }

    /**
     * 展示型按钮
     *
     * @param itemStacks 物品
     * @return {@link Button}
     */
    @NonNull
    static List<@NonNull Button> ofItemStacks(@NonNull Collection<@Nullable ItemStack> itemStacks) {
        return itemStacks.stream().map(e -> (PlayerItemInterface) player -> e).map(Button::of).collect(Collectors.toList());
    }

    /**
     * 点击型按钮
     *
     * @param itemStacks 物品
     * @param map        映射
     * @param <IMPL>     实现了{@link PlayerItemInterface}和{@link PlayerClickInterface}的类
     * @return {@link Button}
     */
    @NonNull
    static <@NonNull IMPL extends PlayerItemInterface & PlayerClickInterface>
    List<@NonNull Button> ofItemStacksInstances(@NonNull Collection<@Nullable ItemStack> itemStacks, @NonNull Function<@Nullable ItemStack, @NonNull IMPL> map) {
        return itemStacks.stream().map(map).map(Button::ofInstance).collect(Collectors.toList());
    }

    @Contract(value = " -> !null", pure = true)
    static Button empty() {
        return EMPTY;
    }
}
