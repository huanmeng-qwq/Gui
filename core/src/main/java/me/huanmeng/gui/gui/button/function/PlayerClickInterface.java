package me.huanmeng.gui.gui.button.function;

import me.huanmeng.gui.gui.button.ClickData;
import me.huanmeng.gui.gui.enums.Result;
import java.util.function.Consumer;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Functional interface for handling button click events.
 * <p>
 * This is the core interface for processing player interactions with buttons in GUIs.
 * Implementations receive comprehensive click data and return a result indicating how
 * the event should be handled.
 *
 * <p>
 * The return value determines:
 * <ul>
 *   <li>{@link Result#CANCEL} - Cancel the inventory event (prevent vanilla behavior)</li>
 *   <li>{@link Result#ALLOW} - Allow the inventory event to proceed</li>
 *   <li>{@link Result#CANCEL_UPDATE} - Cancel the event and refresh the clicked button</li>
 *   <li>{@link Result#CANCEL_UPDATE_ALL} - Cancel the event and refresh the entire GUI</li>
 * </ul>
 *
 *
 * @author huanmeng_qwq
 * @since 2023/3/17
 */
@FunctionalInterface
public interface PlayerClickInterface {

    /**
     * Handles a button click event.
     *
     * @param clickData comprehensive data about the click event
     * @return the result indicating how to handle the event
     */
    Result onClick(@NonNull ClickData clickData);

    /**
     * Creates a click handler that always returns the specified result with no additional logic.
     *
     * @param result the result to return
     * @return a PlayerClickInterface that returns the specified result
     */
    static PlayerClickInterface of(Result result) {
        return new DefaultClickHandler(result);
    }

    /**
     * Creates a click handler that executes a consumer and returns the specified result.
     *
     * @param result the result to return after executing the consumer
     * @param clickDataConsumer the consumer to execute with the click data
     * @return a PlayerClickInterface that executes the consumer and returns the result
     */
    static PlayerClickInterface of(Result result, Consumer<ClickData> clickDataConsumer) {
        return new DefaultClickHandler(result, clickDataConsumer);
    }

    /**
     * Creates a click handler that executes a player-based consumer and returns the specified result.
     * <p>
     * This is a convenience method that extracts the player from the click data before
     * passing it to the consumer.
     * </p>
     *
     * @param result the result to return after executing the consumer
     * @param clickDataConsumer the consumer to execute with the player
     * @return a PlayerClickInterface that executes the consumer and returns the result
     */
    static PlayerClickInterface handlePlayerClick(Result result, Consumer<Player> clickDataConsumer) {
        return new DefaultClickHandler(result, clickData -> clickDataConsumer.accept(clickData.player));
    }

    /**
     * Default implementation of PlayerClickInterface that executes an optional consumer
     * and returns a specified result.
     */
    class DefaultClickHandler implements PlayerClickInterface {
        /**
         * Optional consumer to execute on click.
         */
        Consumer<ClickData> clickConsumer = null;

        /**
         * The result to return after handling the click.
         */
        final Result result;

        /**
         * Constructs a handler that only returns a result.
         *
         * @param result the result to return
         */
        DefaultClickHandler(Result result) {
            this.result = result;
        }

        /**
         * Constructs a handler that executes a consumer and returns a result.
         *
         * @param result the result to return
         * @param clickConsumer the consumer to execute
         */
        DefaultClickHandler(Result result, Consumer<ClickData> clickConsumer) {
            this.result = result;
            this.clickConsumer = clickConsumer;
        }

        /**
         * {@inheritDoc}
         * <p>
         * Executes the consumer if present, then returns the configured result.
         * </p>
         */
        @Override
        public Result onClick(@NonNull ClickData clickData) {
            if (clickConsumer != null) {
                clickConsumer.accept(clickData);
            }
            return this.result;
        }
    }
}
