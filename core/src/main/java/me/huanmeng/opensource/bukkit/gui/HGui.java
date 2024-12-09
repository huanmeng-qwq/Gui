package me.huanmeng.opensource.bukkit.gui;

import me.huanmeng.opensource.bukkit.gui.impl.GuiCustom;
import me.huanmeng.opensource.bukkit.gui.impl.GuiPage;
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
 * 2023/3/17<br>
 * Gui<br>
 * <p>
 * 简易的Gui包装类
 *
 * @author huanmeng_qwq
 */
@SuppressWarnings({"unused"})
public abstract class HGui {
    @Nullable
    protected HGui from;
    @Nullable
    protected AbstractGui<?> fromGui;
    @NonNull
    protected final PackageGuiContext context;
    protected boolean allowBack;

    @Nullable
    protected MethodHandle constructorHandle;

    protected BiFunction<Player, Boolean, List<Object>> newInstanceValuesFunction;

    public static final Map<Player, Node> BACK_NODE_MAP = new ConcurrentHashMap<>();

    public HGui(@NonNull Player player) {
        this(player, false);
        setConstructor(MethodType.methodType(void.class, Player.class), Arrays::asList);
    }

    public HGui(@NonNull Player player, boolean allowBack) {
        this.context = new PackageGuiContext(player);
        this.allowBack = allowBack;
        setConstructor(MethodType.methodType(void.class, Player.class, boolean.class), Arrays::asList);
    }

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

    public void setNewInstanceValuesFunction(BiFunction<Player, Boolean, List<Object>> newInstanceValuesFunction) {
        this.newInstanceValuesFunction = newInstanceValuesFunction;
    }

    protected void setConstructor(Constructor<?> constructor, BiFunction<Player, Boolean, List<Object>> newInstanceValuesFunction) {
        this.newInstanceValuesFunction = newInstanceValuesFunction;
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            constructorHandle = lookup.unreflectConstructor(constructor);
        } catch (IllegalAccessException e) {
            constructorHandle = null;
        }
    }

    @Nullable
    protected abstract AbstractGui<?> gui();

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

    protected void whenOpen() {

    }

    @Nullable
    protected HGui from() {
        return from;
    }

    @Nullable
    protected AbstractGui<?> getFromGui() {
        return fromGui;
    }

    public static class Node {
        private Node prev, next;
        private MethodHandle methodHandle;
        private BiFunction<Player, Boolean, List<Object>> newInstanceValuesFunction;
    }
}
