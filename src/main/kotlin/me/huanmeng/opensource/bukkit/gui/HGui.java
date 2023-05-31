package me.huanmeng.opensource.bukkit.gui;

import me.huanmeng.opensource.bukkit.gui.impl.GuiCustom;
import org.bukkit.entity.Player;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * 2023/3/17<br>
 * Gui<br>
 * <p>
 * 建议的Gui包装类
 *
 * @author huanmeng_qwq
 */
@SuppressWarnings({"rawtypes", "unused"})
public abstract class HGui {
    private HGui from;
    protected final PackageGuiContext context;
    protected boolean allowBack;

    private final MethodHandle constructorHandle;

    public HGui(Player player) {
        this(player, false);
    }

    public HGui(Player player, boolean allowBack) {
        this.context = new PackageGuiContext(player);
        this.allowBack = allowBack;
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            constructorHandle = lookup.findConstructor(getClass(), MethodType.methodType(void.class, Player.class, boolean.class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract AbstractGui<? super GuiCustom> gui();

    public final void open() {
        AbstractGui g = gui();
        if (g == null) {
            return;
        }
        context.gui(g);
        if (allowBack) {
            g.backRunner(() -> {
                HGui gui;
                try {
                    gui = (HGui) constructorHandle.invokeExact(context.getPlayer(), allowBack);
                    gui.from = HGui.this;
                    gui.open();
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            });
        }
        g.openGui();
        whenOpen();
    }

    protected void whenOpen() {

    }

    protected HGui from() {
        return from;
    }
}
