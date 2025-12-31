package me.huanmeng.gui.gui;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
@SuppressWarnings("rawtypes")
public class PackageGuiContext {
    private final Map<String, Object> metadata = new HashMap<>(1);
    @NonNull
    private final Player player;
    private AbstractGui gui;

    public PackageGuiContext(@NonNull Player player) {
        this.player = player;
    }

    @NonNull
    public Player getPlayer() {
        return player;
    }

    @Nullable
    public AbstractGui getGui() {
        return gui;
    }

    public void gui(@NonNull AbstractGui gui) {
        this.gui = gui;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }
}
