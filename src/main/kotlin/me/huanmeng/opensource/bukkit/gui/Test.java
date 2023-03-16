package me.huanmeng.opensource.bukkit.gui;

import me.huanmeng.opensource.bukkit.gui.button.Button;
import me.huanmeng.opensource.bukkit.gui.button.function.PlayerClickCancelUpdateAllInterface;
import me.huanmeng.opensource.bukkit.gui.enums.Result;
import me.huanmeng.opensource.bukkit.gui.impl.GuiCustom;
import me.huanmeng.opensource.bukkit.gui.impl.GuiPage;
import me.huanmeng.opensource.bukkit.gui.slot.Slot;
import me.huanmeng.opensource.bukkit.gui.slot.Slots;
import me.huanmeng.opensource.bukkit.util.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
public class Test extends JavaPlugin {
    @Override
    public void onEnable() {
        new GuiManager(this);
    }

    public void test(Player player) {
        GuiCustom gui = new GuiCustom(player);
        // Gui行数为3行
        gui.line(3);
        // Gui标题
        gui.title("Test Gui");
        // 开始绘制按钮
        gui.draw().set(Slot.of(0), /*图标*/Button.of(p -> {
            return new ItemBuilder(Material.DIRT).setName("§b点击获取泥土").build();
        }, /*点击时触发*/p -> {
            p.getInventory().addItem(new ItemStack(Material.DIRT));
        }));

        gui.draw().set(Slot.of(1), /*图标*/Button.of(p -> {
            return new ItemBuilder(Material.DIRT).setName("§b左击获取泥土").addLore("§e右键获取火把").build();
        },/*点击时触发*/ (p, click, action, slotType, slot, hotBarKey) -> {
            if (click.isLeftClick()) {
                p.getInventory().addItem(new ItemStack(Material.DIRT));
            } else {
                p.getInventory().addItem(new ItemStack(Material.TORCH));
            }
        }));

        Random random = new Random();
        List<Material> list = Arrays.asList(Material.DIRT, Material.APPLE, Material.DIAMOND, Material.EMERALD);
        gui.draw().set(Slot.of(2), new Button() {
            private ItemStack itemStack;

            @Override
            public ItemStack getShowItem(Player player) {
                Material randomEle = list.get(random.nextInt(list.size()));
                return itemStack = new ItemBuilder(randomEle).setName("§a点击随机获取物品").build();
            }

            @Override
            public Result onClick(Slot slot, Player player, ClickType click, InventoryAction action, InventoryType.SlotType slotType, int slotKey, int hotBarKey, InventoryClickEvent e) {
                player.getInventory().addItem(itemStack);
                return Result.CANCEL_UPDATE;
            }
        });
        // 设置Gui每多少tick执行一次额外添加的tick方法(见下一行) - 默认5秒(100tick)
        gui.tick(10);
        gui.addTick(g -> {
            // 刷新第三格的物品
            g.refresh(Slots.of(2));
        });

        gui.draw().set(
                Slots.pattern(new String[]{
                        "---------",
                        "-xxxxxxx-",
                        "---------"
                }, 'x'),
                /*这里也可以传List<Button> 每个element将对应每个x所在位置*/
                Button.of(p -> new ItemBuilder(Material.DIAMOND_SWORD).setName("§b点击获取钻石剑").build(), p -> p.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD)))
        );


        // 为玩家打开Gui
        gui.openGui();
    }

    public void testPage(Player player) {
        List<Button> buttons = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            buttons.add(Button.of(p -> new ItemBuilder(Material.DIRT).setName("§b点击获取泥土").build(), p -> p.getInventory().addItem(new ItemStack(Material.DIRT))));
        }
        GuiPage gui = new GuiPage(player, buttons, 18, Slots.PATTERN_LINE_DEFAULT);
        gui.line(4);
        gui.tick(10);
        gui.addTick(g -> g.refresh(true));
        gui.addAttachedButton(
                new GuiButton(
                        Slot.ofGame(1, 4),
                        Button.of(
                                p -> {
                                    if (gui.pagination().hasLast(gui.page())) {
                                        return new ItemBuilder(Material.ARROW, "§a下一页").build();
                                    } else {
                                        return new ItemStack(Material.AIR);
                                    }
                                },
                                /*点击后刷新所有按钮*/
                                (PlayerClickCancelUpdateAllInterface) (p, click, action, slotType, slot, hotBarKey) -> {
                                    if (gui.pagination().hasLast(gui.page())) {
                                        gui.page(gui.page() - 1);
                                    }
                                }
                        )
                )
        );
        gui.addAttachedButton(
                new GuiButton(
                        Slot.ofGame(9, 4),
                        Button.of(
                                p -> {
                                    if (gui.pagination().hasNext(gui.page())) {
                                        return new ItemBuilder(Material.ARROW, "§a下一页").build();
                                    } else {
                                        return new ItemStack(Material.AIR);
                                    }
                                },
                                /*点击后刷新所有按钮*/
                                (PlayerClickCancelUpdateAllInterface) (p, click, action, slotType, slot, hotBarKey) -> {
                                    if (gui.pagination().hasNext(gui.page())) {
                                        gui.page(gui.page() + 1);
                                    }
                                }
                        )
                )
        );
        gui.title("Test Page");
        gui.openGui();
    }
}
