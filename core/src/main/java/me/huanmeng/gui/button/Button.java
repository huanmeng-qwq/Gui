package me.huanmeng.gui.button;

import me.huanmeng.gui.button.function.PlayerClickInterface;
import me.huanmeng.gui.button.function.PlayerItemInterface;
import me.huanmeng.gui.enums.Result;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.Contract;

/**
 * The core interface representing a clickable button in a GUI inventory.
 * <p>
 * A button combines visual representation (ItemStack) with click behavior. It can be used to create
 * both display-only items and interactive elements in custom inventory GUIs.
 *
 * <p>
 * This is a functional interface whose functional method is {@link #getShowItem(Player)}.
 *
 *
 * @author huanmeng_qwq
 * @since 2023/3/17
 */
@SuppressWarnings("unused")
@FunctionalInterface
public interface Button {
    /**
     * Gets the ItemStack to display for this button in the inventory.
     * <p>
     * This method is called when the GUI needs to render the button. It allows for dynamic
     * item display based on the player viewing the GUI.
     * </p>
     *
     * @param player the player viewing the button
     * @return the ItemStack to display, or null to show no item
     */
    @Nullable
    ItemStack getShowItem(@NonNull Player player);

    /**
     * An empty button constant that displays no item.
     */
    Button EMPTY = (p) -> null;

    /**
     * Handles click events on this button.
     * <p>
     * The default implementation returns {@link Result#CANCEL}, which prevents the vanilla
     * inventory behavior (such as picking up the item).
     * </p>
     *
     * @param clickData the click event data containing player, GUI, slot, and event information
     * @return the result indicating whether to cancel the event and whether to update the GUI
     */
    @NonNull
    default Result onClick(@NonNull ClickData clickData) {
        return Result.CANCEL;
    }


    /**
     * Creates a display-only button with a dynamic item provider.
     * <p>
     * The button will display the item returned by the provider but will not respond to clicks
     * beyond canceling the default inventory behavior.
     * </p>
     *
     * @param item the item provider that supplies the ItemStack based on the viewing player
     * @return a new display-only Button
     */
    @Contract(value = "!null -> new", pure = true)
    static Button of(PlayerItemInterface item) {
        return new BaseButton(item, null);
    }

    /**
     * Creates a display-only button with a static ItemStack.
     * <p>
     * The button will display the same item for all players and will not respond to clicks
     * beyond canceling the default inventory behavior.
     * </p>
     *
     * @param item the ItemStack to display
     * @return a new display-only Button
     */
    @Contract(value = "_ -> new", pure = true)
    static Button of(ItemStack item) {
        return new BaseButton(PlayerItemInterface.of(item), null);
    }

    /**
     * Creates a clickable button with a static ItemStack and click handler.
     *
     * @param item the ItemStack to display
     * @param clickable the click handler that processes click events
     * @return a new clickable Button
     */
    @Contract(value = "_,_ -> new", pure = true)
    static Button of(ItemStack item, PlayerClickInterface clickable) {
        return new BaseButton(PlayerItemInterface.of(item), clickable);
    }

    /**
     * Creates a clickable button with a dynamic item provider and click handler.
     *
     * @param item the item provider that supplies the ItemStack based on the viewing player
     * @param clickable the click handler that processes click events
     * @return a new clickable Button
     */
    @Contract(value = "_, _ -> new", pure = true)
    static Button of(PlayerItemInterface item, PlayerClickInterface clickable) {
        return new BaseButton(item, clickable);
    }

    /**
     * Creates a clickable button with a dynamic item provider and player-based click consumer.
     * <p>
     * This is a convenience method that wraps a simple player consumer with the specified result.
     * </p>
     *
     * @param item the item provider that supplies the ItemStack based on the viewing player
     * @param result the result to return from the click handler (e.g., CANCEL, CANCEL_UPDATE)
     * @param clickable the consumer that processes the player when clicked
     * @return a new clickable Button
     */
    @Contract(value = "null, !null, !null -> new", pure = true)
    static Button ofPlayerClick(PlayerItemInterface item, Result result, Consumer<Player> clickable) {
        return new BaseButton(item, PlayerClickInterface.handlePlayerClick(result, clickable));
    }

    /**
     * Creates a clickable button with a dynamic item provider and player-based click consumer.
     * <p>
     * This button will automatically cancel the inventory event when clicked.
     * </p>
     *
     * @param item the item provider that supplies the ItemStack based on the viewing player
     * @param clickable the consumer that processes the player when clicked
     * @return a new clickable Button
     */
    @Contract(value = "null, !null, -> new", pure = true)
    static Button ofPlayerClick(PlayerItemInterface item, Consumer<Player> clickable) {
        return ofPlayerClick(item, Result.CANCEL, clickable);
    }

    /**
     * Creates a clickable button with a static ItemStack and player-based click consumer.
     *
     * @param item the ItemStack to display
     * @param result the result to return from the click handler (e.g., CANCEL, CANCEL_UPDATE)
     * @param clickable the consumer that processes the player when clicked
     * @return a new clickable Button
     */
    @Contract(value = "null, !null, !null -> new", pure = true)
    static Button ofPlayerClick(ItemStack item, Result result, Consumer<Player> clickable) {
        return new BaseButton(PlayerItemInterface.of(item), PlayerClickInterface.handlePlayerClick(result, clickable));
    }

    /**
     * Creates a clickable button with a static ItemStack and player-based click consumer.
     * <p>
     * This button will automatically cancel the inventory event when clicked.
     * </p>
     *
     * @param item the ItemStack to display
     * @param clickable the consumer that processes the player when clicked
     * @return a new clickable Button
     */
    @Contract(value = "null, !null, -> new", pure = true)
    static Button ofPlayerClick(ItemStack item, Consumer<Player> clickable) {
        return ofPlayerClick(item, Result.CANCEL, clickable);
    }


    /**
     * Creates a button from an implementation that provides both item display and click handling.
     * <p>
     * This method is useful when you have a single class that implements both {@link PlayerItemInterface}
     * and {@link PlayerClickInterface}.
     * </p>
     *
     * @param impl the implementation providing both item display and click handling
     * @param <IMPL> the type implementing both interfaces
     * @return a new Button
     */
    @Contract(value = "null -> new", pure = true)
    static <@NonNull IMPL extends PlayerItemInterface & PlayerClickInterface> Button ofInstance(IMPL impl) {
        return new BaseButton(impl, impl);
    }

    /**
     * Creates a list of buttons from a collection of implementations.
     * <p>
     * Each implementation must provide both item display and click handling.
     * </p>
     *
     * @param impl the collection of implementations
     * @param <IMPL> the type implementing both interfaces
     * @return a list of Buttons
     */
    @NonNull
    static <@NonNull IMPL extends PlayerItemInterface & PlayerClickInterface> List<Button> ofInstances(@NonNull Collection<@NonNull IMPL> impl) {
        return impl.stream().map(Button::ofInstance).collect(Collectors.toList());
    }

    /**
     * Creates a list of buttons from an array of implementations.
     * <p>
     * Each implementation must provide both item display and click handling.
     * </p>
     *
     * @param impl the array of implementations
     * @param <IMPL> the type implementing both interfaces
     * @return a list of Buttons
     */
    @SafeVarargs
    static <@NonNull IMPL extends PlayerItemInterface & PlayerClickInterface> List<Button> ofInstancesArray(@NonNull IMPL... impl) {
        return Arrays.stream(impl).map(Button::ofInstance).collect(Collectors.toList());
    }

    /**
     * Converts a collection of ItemStacks to buttons using a custom mapping function.
     * <p>
     * This method allows you to transform ItemStacks into buttons with custom behavior.
     * The mapping function can return null to create empty slots.
     * </p>
     *
     * @param itemStacks the collection of ItemStacks to convert
     * @param map the function that maps each ItemStack to a Button
     * @return a list of Buttons (may contain nulls)
     */
    @NonNull
    static List<@Nullable Button> ofItemStacksButton(@NonNull Collection<@Nullable ItemStack> itemStacks, @NonNull Function<@Nullable ItemStack, @Nullable Button> map) {
        return itemStacks.stream().map(map).collect(Collectors.toList());
    }

    /**
     * Converts a collection of ItemStacks to display-only buttons using a custom item provider.
     * <p>
     * This method allows you to transform ItemStacks into dynamic item providers before
     * creating display-only buttons.
     * </p>
     *
     * @param itemStacks the collection of ItemStacks to convert
     * @param map the function that maps each ItemStack to a PlayerItemInterface
     * @return a list of display-only Buttons
     */
    @NonNull
    static List<@NonNull Button> ofItemStacks(@NonNull Collection<@Nullable ItemStack> itemStacks, @NonNull Function<@Nullable ItemStack, @NonNull PlayerItemInterface> map) {
        return itemStacks.stream().map(map).map(Button::of).collect(Collectors.toList());
    }

    /**
     * Converts a collection of ItemStacks to display-only buttons.
     * <p>
     * This is a convenience method that creates simple display buttons for each ItemStack.
     * </p>
     *
     * @param itemStacks the collection of ItemStacks to convert
     * @return a list of display-only Buttons
     */
    @NonNull
    static List<@NonNull Button> ofItemStacks(@NonNull Collection<@Nullable ItemStack> itemStacks) {
        return itemStacks.stream().map(e -> (PlayerItemInterface) player -> e).map(Button::of).collect(Collectors.toList());
    }

    /**
     * Converts a collection of ItemStacks to clickable buttons using a custom implementation factory.
     * <p>
     * This method allows you to transform ItemStacks into full button implementations that provide
     * both display and click handling.
     * </p>
     *
     * @param itemStacks the collection of ItemStacks to convert
     * @param map the function that maps each ItemStack to an implementation
     * @param <IMPL> the type implementing both interfaces
     * @return a list of clickable Buttons
     */
    @NonNull
    static <@NonNull IMPL extends PlayerItemInterface & PlayerClickInterface>
    List<@NonNull Button> ofItemStacksInstances(@NonNull Collection<@Nullable ItemStack> itemStacks, @NonNull Function<@Nullable ItemStack, @NonNull IMPL> map) {
        return itemStacks.stream().map(map).map(Button::ofInstance).collect(Collectors.toList());
    }

    /**
     * Returns an empty button that displays no item.
     *
     * @return the empty button constant
     */
    @Contract(value = " -> !null", pure = true)
    static Button empty() {
        return EMPTY;
    }
}
