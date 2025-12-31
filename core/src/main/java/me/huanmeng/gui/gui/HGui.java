package me.huanmeng.gui.gui;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * High-level GUI wrapper providing convenient methods for GUI creation and navigation history.
 *
 * <p>This abstract class provides:
 * <ul>
 *   <li>Automatic back button navigation support using a stack structure</li>
 *   <li>Context management for player-specific GUI state</li>
 *   <li>Constructor reflection for creating new instances when navigating back</li>
 *   <li>Page state preservation when navigating between paginated GUIs</li>
 * </ul>
 *
 * <p><b>Navigation System:</b>
 * <br>The navigation uses a simple stack model:
 * <ul>
 *   <li>Stack stores GUIs that can be returned to (NOT the current GUI)</li>
 *   <li>When opening a new GUI from an existing one, the current GUI is pushed to stack</li>
 *   <li>When going back, pop from stack and rebuild that GUI</li>
 * </ul>
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
     * The previous HGui in the navigation history (set when navigating back).
     */
    @Nullable
    protected HGui from;

    /**
     * The AbstractGui instance from the previous GUI (for page state preservation).
     */
    @Nullable
    protected AbstractGui<?> fromGui;

    /**
     * Context containing player-specific information and the GUI instance.
     */
    @NonNull
    protected final PackageGuiContext context;

    /**
     * Whether this GUI supports back navigation.
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
     * Global map storing navigation history stack for each player.
     * <br>Stack contains GUIs that can be returned to (NOT the current GUI).
     */
    public static final Map<Player, Deque<Node>> BACK_STACK = new ConcurrentHashMap<>();

    /**
     * Creates a new HGui without back navigation support.
     *
     * @param player the player who will view this GUI
     */
    public HGui(@NonNull Player player) {
        this(player, false);
        initConstructor(MethodType.methodType(void.class, Player.class), Arrays::asList);
    }

    /**
     * Creates a new HGui with optional back navigation support.
     *
     * @param player    the player who will view this GUI
     * @param allowBack whether to enable back navigation to previous GUIs
     */
    public HGui(@NonNull Player player, boolean allowBack) {
        this.context = new PackageGuiContext(player);
        this.allowBack = allowBack;
        initConstructor(MethodType.methodType(void.class, Player.class, boolean.class), Arrays::asList);
    }

    /**
     * Initializes the constructor handle for this GUI class.
     */
    private void initConstructor(MethodType methodType, BiFunction<Player, Boolean, List<Object>> argsFunction) {
        this.newInstanceValuesFunction = argsFunction;
        try {
            Class<?>[] paramTypes = methodType.parameterArray();
            Constructor<?> constructor = getClass().getDeclaredConstructor(paramTypes);
            constructor.setAccessible(true);
            this.constructorHandle = MethodHandles.lookup().unreflectConstructor(constructor);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            this.constructorHandle = null;
        }
    }

    /**
     * Sets the constructor information for recreating this GUI instance.
     *
     * @param methodType   the method type signature of the constructor
     * @param argsFunction function that provides constructor arguments
     */
    @SuppressWarnings("SameParameterValue")
    protected void setConstructor(MethodType methodType, BiFunction<Player, Boolean, List<Object>> argsFunction) {
        initConstructor(methodType, argsFunction);
    }

    /**
     * Sets the constructor information using a Constructor object.
     *
     * @param constructor  the constructor to use for recreating instances
     * @param argsFunction function that provides constructor arguments
     */
    protected void setConstructor(Constructor<?> constructor, BiFunction<Player, Boolean, List<Object>> argsFunction) {
        this.newInstanceValuesFunction = argsFunction;
        try {
            constructor.setAccessible(true);
            this.constructorHandle = MethodHandles.lookup().unreflectConstructor(constructor);
        } catch (IllegalAccessException e) {
            this.constructorHandle = null;
        }
    }

    /**
     * Creates and returns the underlying AbstractGui instance.
     * <br>Subclasses must implement this to define the GUI content.
     *
     * @return the GUI to display, or null if the GUI cannot be created
     */
    @Nullable
    protected abstract AbstractGui<?> gui();

    /**
     * Opens this GUI for the player.
     */
    public final void open() {
        // Get the current HGui from the currently open GUI (if any)
        HGui fromHGui = null;
        AbstractGui<?> currentGui = GuiManager.instance().getUserOpenGui(context.getPlayer().getUniqueId());
        if (currentGui != null && currentGui.metadata.containsKey("wrapper")) {
            fromHGui = (HGui) currentGui.metadata.get("wrapper");
        }
        openInternal(false, fromHGui);
    }

    /**
     * Internal method to open the GUI.
     *
     * @param isBack   whether this is a back navigation
     * @param fromHGui the HGui we are navigating from (to push to stack), or null
     */
    private void openInternal(boolean isBack, @Nullable HGui fromHGui) {
        AbstractGui<?> g = gui();
        if (g == null) {
            return;
        }

        g.setPlayer(context.getPlayer());
        context.gui(g);

        // Push the fromHGui to stack if provided and not back navigation
        if (!isBack && fromHGui != null && fromHGui.constructorHandle != null && allowBack) {
            Deque<Node> stack = BACK_STACK.computeIfAbsent(context.getPlayer(), k -> new ArrayDeque<>());
            stack.push(new Node(fromHGui.constructorHandle, fromHGui.newInstanceValuesFunction, fromHGui.context.getMetadata()));
        }

        // Store reference to this wrapper
        g.metadata.put("wrapper", this);

        // Setup back navigation handler
        setupBackHandler(g);

        // Setup cleanup on close
        setupCloseHandler(g);

        // Open the GUI
        g.openGui();
        whenOpen();
    }

    /**
     * Sets up the back navigation handler.
     */
    private void setupBackHandler(AbstractGui<?> g) {
        g.backRunner(() -> {
            Player player = context.getPlayer();
            Deque<Node> stack = BACK_STACK.get(player);

            // No history to go back to
            if (stack == null || stack.isEmpty()) {
                BACK_STACK.remove(player);
                g.close(false, true);
                return;
            }

            try {
                // Pop and rebuild the previous GUI
                Node prev = stack.pop();
                HGui prevGui = rebuildGui(prev, player);

                // Set navigation context (for page state preservation)
                prevGui.from = this;
                prevGui.fromGui = g;
                prevGui.context.getMetadata().putAll(prev.metadata);

                // Open the previous GUI (isBack=true, no fromHGui needed)
                prevGui.openInternal(true, null);
            } catch (Throwable e) {
                throw new RuntimeException("Failed to navigate back", e);
            }
        });
    }

    /**
     * Rebuilds a GUI from a stack node.
     */
    private HGui rebuildGui(Node node, Player player) throws Throwable {
        MethodHandle handle = node.methodHandle;
        BiFunction<Player, Boolean, List<Object>> argsFunc = node.newInstanceValuesFunction;

        if (argsFunc != null) {
            List<Object> args = argsFunc.apply(player, true);
            return (HGui) handle.invokeWithArguments(args);
        } else {
            return (HGui) handle.invoke(player, true);
        }
    }

    /**
     * Sets up cleanup when GUI is closed.
     */
    private void setupCloseHandler(AbstractGui<?> g) {
        g.whenClose(gui -> g.scheduler().runLater(() -> {
            Player player = context.getPlayer();
            UUID uuid = player.getUniqueId();

            // If player has no GUI open, clean up the stack
            if (!GuiManager.instance().isOpenGui(uuid)) {
                BACK_STACK.remove(player);
            }
        }, 1));
    }

    /**
     * Callback invoked after the GUI is opened.
     * <br>Override this method to perform actions after the GUI is displayed.
     */
    protected void whenOpen() {
    }

    /**
     * Returns the previous HGui in the navigation history.
     *
     * @return the previous HGui, or null if this is the first GUI
     */
    @Nullable
    protected HGui from() {
        return from;
    }

    /**
     * Returns the AbstractGui instance from the previous GUI.
     *
     * @return the previous AbstractGui, or null if this is the first GUI
     */
    @Nullable
    protected AbstractGui<?> getFromGui() {
        return fromGui;
    }

    /**
     * Clears the navigation stack for a player.
     * <br>Call this when you want to reset the navigation history.
     *
     * @param player the player whose stack to clear
     */
    public static void clearStack(Player player) {
        BACK_STACK.remove(player);
    }

    /**
     * Gets the current stack size for a player.
     *
     * @param player the player
     * @return the number of GUIs in the back stack
     */
    public static int getStackSize(Player player) {
        Deque<Node> stack = BACK_STACK.get(player);
        return stack == null ? 0 : stack.size();
    }

    /**
     * Navigation history node containing constructor information.
     */
    public static class Node {
        private final MethodHandle methodHandle;
        private final BiFunction<Player, Boolean, List<Object>> newInstanceValuesFunction;
        private final Map<String, Object> metadata;

        public Node(final MethodHandle methodHandle, final BiFunction<Player, Boolean, List<Object>> newInstanceValuesFunction, final Map<String, Object> metadata) {
            this.methodHandle = methodHandle;
            this.newInstanceValuesFunction = newInstanceValuesFunction;
            this.metadata = metadata;
        }
    }
}
