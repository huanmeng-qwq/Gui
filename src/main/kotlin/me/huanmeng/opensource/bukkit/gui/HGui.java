package me.huanmeng.opensource.bukkit.gui;

import me.huanmeng.opensource.bukkit.gui.impl.GuiCustom;
import me.huanmeng.opensource.bukkit.util.Pair;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.util.*;
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

    static final Map<Player, List<Pair<MethodHandle, BiFunction<Player, Boolean, List<Object>>>>> backMap = new ConcurrentHashMap<>();

    public HGui(@NonNull Player player) {
        this(player, false);
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
    protected abstract AbstractGui<? super GuiCustom> gui();

    public final void open() {
        AbstractGui<?> g = gui();
        if (g == null) {
            return;
        }
        g.setPlayer(context.getPlayer());
        context.gui(g);
        if ((allowBack && constructorHandle != null)) {
            if (!backMap.containsKey(context.getPlayer())) {
                backMap.put(context.getPlayer(), new ArrayList<>());
            }
            if (from == null) {
                backMap.get(context.getPlayer()).add(new Pair<>(constructorHandle, newInstanceValuesFunction));
            }
        }
        g.metadata.put("wrapper", this);
        g.backRunner(() -> {
            List<Pair<MethodHandle, BiFunction<Player, Boolean, List<Object>>>> list = backMap.get(context.getPlayer());
            if ((list == null || list.size() <= (list.size() == 1 ? list.get(0).getA().equals(constructorHandle) ? 1 : 0 : 0))) {
                g.close(false, true);
                backMap.remove(g.player);
                return;
            }
            try {
                Pair<MethodHandle, BiFunction<Player, Boolean, List<Object>>> pair = backMap.get(context.getPlayer()).remove(backMap.get(context.getPlayer()).size() - 2);
                MethodHandle methodHandle = pair.getA();
                HGui gui;
                if (pair.getB() != null) {
                    gui = (HGui) methodHandle.invokeWithArguments(pair.getB().apply(context.getPlayer(), true));
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
                backMap.remove(context.getPlayer());
                return;
            }
            AbstractGui<?> nowGui = GuiManager.instance().getUserOpenGui(uuid);
            if (nowGui == null) {
                return;
            }
            if (!nowGui.metadata.containsKey("wrapper")) {
                backMap.remove(context.getPlayer());
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
}
