package me.huanmeng.opensource.bukkit.component.impl;

import me.huanmeng.opensource.bukkit.component.ComponentConvert;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * 2023/5/31<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
public class DefaultComponentConvertImpl implements ComponentConvert {
    @Override
    @NonNull
    public String convert(@NonNull Player player, @NonNull Component text) {
        return convert(text);
    }

    @Override
    public String convert(@NonNull Component text) {
        return LegacyComponentSerializer.legacySection()
                .serialize(text);
    }
}
