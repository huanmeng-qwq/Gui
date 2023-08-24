package me.huanmeng.opensource.bukkit.component;

import me.huanmeng.opensource.bukkit.component.impl.DefaultComponentConvertImpl;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 2023/5/31<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
public interface ComponentConvert {
    AtomicReference<ComponentConvert> INSTANCE = new AtomicReference<>(new DefaultComponentConvertImpl());

    static ComponentConvert getDefault() {
        return INSTANCE.get();
    }

    /**
     * 将Component转换为字符串型式的文本
     *
     * @param player 玩家
     * @param text   text
     * @return 转换后的文本
     */
    @NonNull
    String convert(@NonNull Player player, @NonNull Component text);

    /**
     * 将Component转换为字符串型式的文本
     *
     * @param text text
     * @return 转换后的文本
     */
    String convert(@NonNull Component text);
}
