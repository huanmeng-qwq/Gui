package me.huanmeng.gui.util.item;

import me.huanmeng.gui.adventure.ComponentConvert;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.map.MapView;
import org.bukkit.potion.PotionEffect;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.*;

/**
 * 2023/8/24<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
public class ItemBuilder {

    private ItemStack is;

    public ItemBuilder(ItemStack is) {
        this.is = is.clone();
    }

    public ItemBuilder(ItemStack is, String name) {
        this.is = is;
        setName(name);
    }

    public ItemBuilder(Material m) {
        is = new ItemStack(m);
    }

    public ItemBuilder(Material m, String name) {
        is = new ItemStack(m);
        setName(name);
    }

    public ItemBuilder(Material m, int amount) {
        is = new ItemStack(m, amount);
    }

    public ItemBuilder(Material m, int amount, byte durability) {
        is = new ItemStack(m, amount, durability);
    }

    public ItemBuilder(Material type, int amount, short durability) {
        is = new ItemStack(type, amount, durability);
    }

    public ItemBuilder clone() {
        return new ItemBuilder(is);
    }

    public ItemBuilder setDurability(short dur) {
        is.setDurability(dur);
        return this;
    }

    public ItemBuilder setDurability(int dur) {
        is.setDurability((short) dur);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        is.setAmount(amount);
        return this;
    }

    public ItemBuilder setName(String str) {
        String name = str;
        ItemMeta im = is.getItemMeta();
        if (name != null && name.isEmpty()) {
            name = "§c";
        }
        if (name == null) {
            im.setDisplayName(null);
        } else {
            im.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        }
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder setName(Component component) {
        return setName(ComponentConvert.getDefault().convert(component));
    }

    public ItemBuilder addLore(List<Component> lores) {
        addLore(lores.stream().map(c -> ComponentConvert.getDefault().convert(c)).toArray(String[]::new));
        return this;
    }

    public ItemBuilder setNameUnColor(String name) {
        ItemMeta im = is.getItemMeta();
        if (name != null && name.isEmpty()) {
            name = "§c";
        }
        if (name == null) {
            im.setDisplayName(null);
        } else {
            im.setDisplayName(name);
        }
        is.setItemMeta(im);
        return this;
    }

    public String getName() {
        ItemMeta im = is.getItemMeta();
        return im != null ? im.getDisplayName() : null;
    }

    public ItemBuilder addFlag(ItemFlag... flags) {
        ItemMeta im = is.getItemMeta();
        im.addItemFlags(flags);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder removeFlag(ItemFlag... flags) {
        ItemMeta im = is.getItemMeta();
        im.removeItemFlags(flags);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder addUnsafeEnchantment(Enchantment ench, int level) {
        if (is.getType() == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta im = (EnchantmentStorageMeta) is.getItemMeta();
            im.addStoredEnchant(ench, level, true);
            is.setItemMeta(im);
            return this;
        } else {
            is.addUnsafeEnchantment(ench, level);
            return this;
        }
    }

    public ItemBuilder addRandomUnsafeEnchantment(int level, Enchantment... ench) {
        is.addUnsafeEnchantment(ench[new Random().nextInt(ench.length)], level);
        return this;
    }

    public ItemBuilder clearLores() {
        ItemMeta im = is.getItemMeta();
        im.setLore(new ArrayList<>());
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder removeEnchantment(Enchantment ench) {
        is.removeEnchantment(ench);
        return this;
    }

    public ItemBuilder removeEnchantments() {
        ItemMeta im = is.getItemMeta();
        im.getEnchants().forEach((enchantment, integer) -> im.removeEnchant(enchantment));
        return this;
    }

    public ItemBuilder setSkullOwner(String owner) {
        try {
            SkullMeta im = (SkullMeta) is.getItemMeta();
            im.setOwner(owner);
            is.setItemMeta(im);
        } catch (ClassCastException ignored) {
        }
        return this;
    }

    public ItemBuilder setSkullOwner(OfflinePlayer player) {
        try {
            SkullMeta im = (SkullMeta) is.getItemMeta();
            Object playerProfile = getPlayerProfileMethod.bindTo(player).invoke();
            setPlayerProfileMethod.bindTo(im).invoke(playerProfile);
            is.setItemMeta(im);
        } catch (Throwable ignored) {
            setSkullOwner(player.getName());
        }
        return this;
    }

    public ItemBuilder setSkullPlayerProfile(Object playerProfile) {
        try {
            SkullMeta im = (SkullMeta) is.getItemMeta();
            setPlayerProfileMethod.bindTo(im).invoke(playerProfile);
            is.setItemMeta(im);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
        return this;
    }

    public ItemBuilder addEnchant(Enchantment ench, int level) {
        if (is.getType() == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta im = (EnchantmentStorageMeta) is.getItemMeta();
            im.addStoredEnchant(ench, level, true);
            is.setItemMeta(im);
            return this;
        } else {
            ItemMeta im = is.getItemMeta();
            im.addEnchant(ench, level, true);
            is.setItemMeta(im);
            return this;
        }
    }

    public ItemBuilder addEnchantments(Map<Enchantment, Integer> enchantments) {
        is.addEnchantments(enchantments);
        return this;
    }

    public ItemBuilder setInfinityDurability() {
        is.setDurability((short) 32767);
        return this;
    }

    public ItemBuilder setUnbreakable() {
        ItemMeta im = is.getItemMeta();
        try {
            itemUnbreakableMethod.bindTo(im).invoke(true);
        } catch (Throwable e) {
            Object itemSpigot = null;
            try {
                itemSpigot = itemSpigotMethod.bindTo(im).invoke();
                itemSpigotUnbreakableMethod.bindTo(itemSpigot).invoke(true);
            } catch (Throwable ignored) {
            }
        }
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder setType(Material material) {
        is.setType(material);
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        ItemMeta im = is.getItemMeta();
        im.setLore(new ArrayList<>(Arrays.asList(lore)));
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        if (lore == null) {
            return this;
        }
        ItemMeta im = is.getItemMeta();
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder removeLoreLine(String line) {
        ItemMeta im = is.getItemMeta();
        List<String> lore = new ArrayList<>(im.getLore());
        if (!lore.contains(line)) {
            return this;
        } else {
            lore.remove(line);
            im.setLore(lore);
            is.setItemMeta(im);
            return this;
        }
    }

    public ItemBuilder removeLoreLine(int index) {
        ItemMeta im = is.getItemMeta();
        List<String> lore = new ArrayList<>(im.getLore());
        if (index >= 0 && index < lore.size()) {
            lore.remove(index);
            im.setLore(lore);
            is.setItemMeta(im);
            return this;
        } else {
            return this;
        }
    }

    public ItemBuilder addLoreLine(String line) {
        ItemMeta im = is.getItemMeta();
        List<String> lore = new ArrayList<>();
        if (im.hasLore()) {
            lore = new ArrayList<>(im.getLore());
        }
        lore.add(ChatColor.translateAlternateColorCodes('&', line));
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder addLoreLine(String line, int pos) {
        ItemMeta im = is.getItemMeta();
        List<String> lore = new ArrayList<>(im.getLore());
        lore.add(pos, line);
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    public int getLores() {
        ItemMeta im = is.getItemMeta();
        return im != null && im.hasLore() ? im.getLore().size() : 0;
    }

    public ItemBuilder addLoreLine(String... lines) {
        for (String line : lines) {
            addLoreLine(line);
        }
        return this;
    }

    public ItemBuilder setDyeColor(DyeColor color) {
        is.setDurability(color.getDyeData());
        return this;
    }

    @Deprecated
    public ItemBuilder setWoolColor(DyeColor color) {
        if (is.getType().name().equals("WOOL") || is.getType().name().endsWith("_WOOL")) {
            is.setDurability(color.getWoolData());
        }
        return this;
    }

    public ItemBuilder setLeatherArmorColor(Color color) {
        try {
            LeatherArmorMeta im = (LeatherArmorMeta) is.getItemMeta();
            im.setColor(color);
            is.setItemMeta(im);
        } catch (ClassCastException ignored) {
        }
        return this;
    }

    public ItemBuilder setMapView(MapView mapView) {
        setDurability(mapView.getId());
        return this;
    }

    public ItemBuilder addPotion(PotionEffect potionEffect) {
        PotionMeta im = (PotionMeta) is.getItemMeta();
        im.addCustomEffect(potionEffect, true);
        is.setItemMeta(im);
        return this;
    }

    public ItemStack build() {
        return is;
    }

    public ItemBuilder addBannerPattern(Pattern pattern) {
        BannerMeta bannerMeta = (BannerMeta) is.getItemMeta();
        bannerMeta.addPattern(pattern);
        is.setItemMeta(bannerMeta);
        return this;
    }

    public ItemBuilder setBannerBaseColor(DyeColor color) {
        BannerMeta bannerMeta = (BannerMeta) is.getItemMeta();
        bannerMeta.setBaseColor(color);
        is.setItemMeta(bannerMeta);
        return this;
    }

    public ItemBuilder addLore(String... s) {
        return addLoreLine(s);
    }

    public ItemBuilder glow() {
        return addEnchant(Enchantment.DURABILITY, 1).addFlag(ItemFlag.HIDE_ENCHANTS);
    }

    public void line() {
        addLore("§h§u§a§n§m§e§n§g");
    }

    public Material getType() {
        return is.getType();
    }

    public ItemBuilder copy() {
        return new ItemBuilder(is.clone());
    }

    private static MethodHandle itemSpigotMethod;
    private static MethodHandle itemSpigotUnbreakableMethod;
    private static MethodHandle itemUnbreakableMethod;

    private static MethodHandle getPlayerProfileMethod;
    private static MethodHandle setPlayerProfileMethod;

    static {
        try {
            itemSpigotMethod = MethodHandles.lookup().unreflect(ItemStack.class.getDeclaredMethod("spigot"));
        } catch (Exception e) {
            itemSpigotMethod = null;
        }

        try {
            itemSpigotUnbreakableMethod = MethodHandles.lookup().unreflect(ItemMeta.class.getDeclaredMethod("spigot").getReturnType().getDeclaredMethod("setUnbreakable", boolean.class));
        } catch (Exception e) {
            itemSpigotUnbreakableMethod = null;
        }

        try {
            itemUnbreakableMethod = MethodHandles.lookup().unreflect(ItemMeta.class.getDeclaredMethod("setUnbreakable", boolean.class));
        } catch (Exception e) {
            itemUnbreakableMethod = null;
        }

        try {
            getPlayerProfileMethod = MethodHandles.lookup().unreflect(OfflinePlayer.class.getDeclaredMethod("getPlayerProfile"));
        } catch (Exception e) {
            getPlayerProfileMethod = null;
        }

        try {
            Class<?> playerProfileClass = Class.forName("org.bukkit.profile.PlayerProfile");
            setPlayerProfileMethod = MethodHandles.lookup().unreflect(SkullMeta.class.getDeclaredMethod("setOwnerProfile", playerProfileClass));
        } catch (Exception e) {
            setPlayerProfileMethod = null;
        }
    }

}
