package me.huanmeng.gui.button.function;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Functional interface for providing ItemStacks to display in button slots.
 * <p>
 * This interface allows for dynamic item display based on the viewing player,
 * enabling player-specific button appearances. It can be used to show different
 * items to different players or to update items based on player state.
 *
 * <p>
 * Implementations should return null if no item should be displayed in the slot.
 *
 *
 * @author huanmeng_qwq
 * @since 2023/3/17
 */
@FunctionalInterface
public interface PlayerItemInterface {
    /**
     * Gets the ItemStack to display for the specified player.
     * <p>
     * This method is called when the GUI needs to render the button. Implementations
     * can return different items based on player permissions, state, or preferences.
     * </p>
     *
     * @param player the player viewing the button
     * @return the ItemStack to display, or null to show no item
     */
    @Nullable
    ItemStack get(@NonNull Player player);

    /**
     * Creates a static item provider that always returns the same ItemStack.
     * <p>
     * This is a convenience method for creating simple buttons that show the same
     * item to all players.
     * </p>
     *
     * @param itemStack the ItemStack to always return
     * @return a PlayerItemInterface that returns the specified ItemStack
     */
    static PlayerItemInterface of(ItemStack itemStack) {
        return new StaticItemProvider(itemStack);
    }

    /**
     * A simple implementation that always returns the same ItemStack.
     */
    class StaticItemProvider implements PlayerItemInterface {
        /**
         * The ItemStack to return.
         */
        @Nullable
        private final ItemStack itemStack;

        /**
         * Constructs a new StaticItemProvider.
         *
         * @param itemStack the ItemStack to return, or null
         */
        public StaticItemProvider(@Nullable final ItemStack itemStack) {
            this.itemStack = itemStack;
        }

        /**
         * {@inheritDoc}
         * <p>
         * Always returns the same ItemStack regardless of the player.
         * </p>
         */
        @Override
        public @Nullable ItemStack get(@NonNull Player player) {
            return this.itemStack;
        }
    }
}
