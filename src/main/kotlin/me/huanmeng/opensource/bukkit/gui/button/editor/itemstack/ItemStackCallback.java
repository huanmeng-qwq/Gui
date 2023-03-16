package me.huanmeng.opensource.bukkit.gui.button.editor.itemstack;

import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

/**
 * 2023/2/11<br>
 * LimeCode<br>
 *
 * @author huanmeng_qwq
 */
public class ItemStackCallback {
    private final String display;
    private final Consumer<ItemStack> consumer;

    public ItemStackCallback(String display, Consumer<ItemStack> consumer) {
        this.display = display;
        this.consumer = consumer;
    }

    public void call(ItemStack itemStack) {
        consumer.accept(itemStack);
    }

    public String display() {
        return display;
    }
}
