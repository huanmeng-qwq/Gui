package me.huanmeng.gui.gui;

import me.huanmeng.gui.gui.impl.GuiPage;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

/**
 * High-level GUI wrapper providing convenient methods for GUI creation and navigation history.
 *
 * <p>This abstract class provides:
 * <ul>
 *   <li>Automatic back button navigation support using a linked node structure</li>
 *   <li>Context management for player-specific GUI state</li>
 *   <li>Constructor reflection for creating new instances when navigating back</li>
 *   <li>Page state preservation when navigating between paginated GUIs</li>
 * </ul>
 *
 * <p><b>Navigation System:</b>
 * <br>When {@code allowBack} is true, this class maintains a linked list of GUI states
 * that allows players to navigate backwards through their GUI history. Each node stores
 * the constructor information needed to recreate the previous GUI.
 *
 * <p><b>Usage Example:</b>
 * <pre>{@code
 * public class MyGui extends HGui {
 *     public MyGui(Player player, boolean allowBack) {
 *         super(player, allowBack);
 *     }
 *
 *     @Override
 *     protected AbstractGui<?> gui() {
 *         GuiCustom gui = new GuiCustom(context.getPlayer());
 *         gui.line(3);
 *         gui.title("My GUI");
 *         return gui;
 *     }
 * }
 *
 * // Open the GUI
 * new MyGui(player, true).open();
 * }</pre>
 *
 * @author huanmeng_qwq
 * @since 2023/3/17
 */
@SuppressWarnings({"unused"})
public abstract class HGui {
    /**
     * The previous GUI in the navigation history, if any.
     */
    @Nullable
    protected HGui from;

    /**
     * The AbstractGui instance from the previous GUI in the navigation history.
     */
    @Nullable
    protected AbstractGui<?> fromGui;

    /**
     * Context containing player-specific information and the GUI instance.
     */
    @NonNull
    protected final PackageGuiContext context;

    /**
     * Whether this GUI supports back navigation to the previous GUI.
     */
    protected boolean allowBack;

    /**
     * Method handle for the constructor, used to recreate instances when navigating back.
     */
    @Nullable
    protected MethodHandle constructorHandle;

    /**
     * Function that provides constructor arguments when creating new instances.
     */
    protected BiFunction<Player, Boolean, List<Object>> newInstanceValuesFunction;

    /**
     * Global map storing navigation history nodes for each player.
     * <br>Maps each player to their current position in the GUI navigation history.
     */
    public static final Map<Player, Node> BACK_NODE_MAP = new ConcurrentHashMap<>();

    /**
     * Creates a new HGui without back navigation support.
     *
     * <p>Equivalent to calling {@code HGui(player, false)}.
     *
     * @param player the player who will view this GUI
     */
    public HGui(@NonNull Player player) {
        this(player, false);
        setConstructor(MethodType.methodType(void.class, Player.class), Arrays::asList);
    }

    /**
     * Creates a new HGui with optional back navigation support.
     *
     * @param player the player who will view this GUI
     * @param allowBack whether to enable back navigation to previous GUIs
     */
    public HGui(@NonNull Player player, boolean allowBack) {
        this.context = new PackageGuiContext(player);
        this.allowBack = allowBack;
        setConstructor(MethodType.methodType(void.class, Player.class, boolean.class), Arrays::asList);
    }

    /**
     * Sets the constructor information for recreating this GUI instance.
     *
     * <p>Used internally to enable back navigation by storing constructor metadata.
     *
     * @param methodType the method type signature of the constructor
     * @param newInstanceValuesFunction function that provides constructor arguments
     */
    @SuppressWarnings("SameParameterValue")
    protected void setConstructor(MethodType methodType, BiFunction<Player, Boolean, List<Object>> newInstanceValuesFunction) {
        this.newInstanceValuesFunction = newInstanceValuesFunction;
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            constructorHandle = lookup.findConstructor(getClass(), methodType);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            constructorHandle = null;
        }
    }

    /**
     * Sets the function that provides constructor arguments when creating new instances.
     *
     * @param newInstanceValuesFunction function that takes a player and allowBack flag,
     *                                   returning a list of constructor arguments
     */
    public void setNewInstanceValuesFunction(BiFunction<Player, Boolean, List<Object>> newInstanceValuesFunction) {
        this.newInstanceValuesFunction = newInstanceValuesFunction;
    }

    /**
     * Sets the constructor information using a Constructor object.
     *
     * @param constructor the constructor to use for recreating instances
     * @param newInstanceValuesFunction function that provides constructor arguments
     */
    protected void setConstructor(Constructor<?> constructor, BiFunction<Player, Boolean, List<Object>> newInstanceValuesFunction) {
        this.newInstanceValuesFunction = newInstanceValuesFunction;
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            constructorHandle = lookup.unreflectConstructor(constructor);
        } catch (IllegalAccessException e) {
            constructorHandle = null;
        }
    }

    /**
     * Creates and returns the underlying AbstractGui instance.
     *
     * <p>This method must be implemented by subclasses to define what GUI
     * should be displayed when this HGui is opened.
     *
     * @return the GUI to display, or null if the GUI cannot be created
     */
    @Nullable
    protected abstract AbstractGui<?> gui();

    /**
     * Opens this GUI for the player.
     *
     * <p>This method:
     * <ul>
     *   <li>Creates the underlying GUI via {@link #gui()}</li>
     *   <li>Preserves page state if navigating between paginated GUIs</li>
     *   <li>Sets up navigation history if back navigation is enabled</li>
     *   <li>Registers back button handler if applicable</li>
     *   <li>Opens the GUI inventory for the player</li>
     *   <li>Calls {@link #whenOpen()} callback</li>
     * </ul>
     */
    public final void open() {
        AbstractGui<?> g = gui();
        if (g == null) {
            return;
        }
        try {
            if (fromGui != null && fromGui instanceof GuiPage && g instanceof GuiPage) {
                ((GuiPage) g).page(Math.min(((GuiPage) fromGui).page(), ((GuiPage) fromGui).pagination().getMaxPage()));
            }
        } catch (Throwable ignored) {
        }
        g.setPlayer(context.getPlayer());
        context.gui(g);
        if ((allowBack && constructorHandle != null)) {
            if (!BACK_NODE_MAP.containsKey(context.getPlayer())) {
                BACK_NODE_MAP.put(context.getPlayer(), new Node());
            } else {
                Node node = BACK_NODE_MAP.get(context.getPlayer());
                node.next = new Node();
                node.next.prev = node;
                BACK_NODE_MAP.put(context.getPlayer(), node.next);
            }
            if (from == null) {
                Node node = BACK_NODE_MAP.get(context.getPlayer());
                node.methodHandle = constructorHandle;
                node.newInstanceValuesFunction = newInstanceValuesFunction;
            }
        }
        g.metadata.put("wrapper", this);
        g.backRunner(() -> {
            Node node = BACK_NODE_MAP.get(context.getPlayer());
            if ((node == null || node.prev == null)) {
                g.close(false, true);
                BACK_NODE_MAP.remove(g.player);
                return;
            }
            try {
                Node prev = node.prev;
                MethodHandle methodHandle = prev.methodHandle;
                HGui gui;
                if (prev.newInstanceValuesFunction != null) {
                    gui = (HGui) methodHandle.invokeWithArguments(prev.newInstanceValuesFunction.apply(context.getPlayer(), true));
                } else {
                    gui = (HGui) methodHandle.invoke(context.getPlayer(), true);
                }
                gui.from = this;
                gui.fromGui = g;
                gui.open();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
        g.whenClose(gui -> g.scheduler().runLater(() -> {
            UUID uuid = context.getPlayer().getUniqueId();
            if (!GuiManager.instance().isOpenGui(uuid)) {
                BACK_NODE_MAP.remove(context.getPlayer());
                return;
            }
            AbstractGui<?> nowGui = GuiManager.instance().getUserOpenGui(uuid);
            if (nowGui == null) {
                return;
            }
            if (!nowGui.metadata.containsKey("wrapper")) {
                BACK_NODE_MAP.remove(context.getPlayer());
            }
        }, 1));
        g.openGui();
        whenOpen();
    }

    /**
     * Callback invoked after the GUI is opened.
     *
     * <p>Override this method to perform actions after the GUI is displayed to the player.
     */
    protected void whenOpen() {

    }

    /**
     * Returns the previous HGui in the navigation history.
     *
     * @return the previous HGui, or null if this is the first GUI in the history
     */
    @Nullable
    protected HGui from() {
        return from;
    }

    /**
     * Returns the AbstractGui instance from the previous GUI in the navigation history.
     *
     * @return the previous AbstractGui, or null if this is the first GUI in the history
     */
    @Nullable
    protected AbstractGui<?> getFromGui() {
        return fromGui;
    }

    /**
     * Navigation history node in the linked list structure.
     *
     * <p>Each node contains:
     * <ul>
     *   <li>References to the previous and next nodes in the history</li>
     *   <li>Constructor information for recreating the GUI at this history point</li>
     *   <li>Function for providing constructor arguments</li>
     * </ul>
     */
    public static class Node {
        /**
         * Previous node in the navigation history.
         */
        private Node prev;

        /**
         * Next node in the navigation history.
         */
        private Node next;

        /**
         * Method handle for the GUI constructor.
         */
        private MethodHandle methodHandle;

        /**
         * Function that provides constructor arguments for recreating the GUI.
         */
        private BiFunction<Player, Boolean, List<Object>> newInstanceValuesFunction;
    }
}
