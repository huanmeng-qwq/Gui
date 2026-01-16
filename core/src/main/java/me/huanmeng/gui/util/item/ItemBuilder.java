package me.huanmeng.gui.util.item;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.map.MapView;
import org.bukkit.potion.PotionEffect;

/**
 * A fluent builder class for creating and modifying Bukkit {@link ItemStack} objects.
 * <p>
 * ItemBuilder provides a convenient chainable API for constructing ItemStacks with various
 * properties such as display names, lore, enchantments, item flags, and specialized metadata
 * for items like skulls, leather armor, banners, and potions.
 * <p>
 * <b>Usage Example:</b>
 * <pre>{@code
 * ItemStack item = new ItemBuilder(Material.DIAMOND_SWORD)
 *     .setName("&6Legendary Sword")
 *     .addLoreLine("&7A powerful weapon")
 *     .addEnchant(Enchantment.DAMAGE_ALL, 5)
 *     .setUnbreakable()
 *     .build();
 * }</pre>
 *
 * @author huanmeng_qwq
 * @since 2023/8/24
 */
public class ItemBuilder {

    /**
     * The ItemStack being built.
     */
    private ItemStack is;

    /**
     * Creates a new ItemBuilder by cloning an existing ItemStack.
     *
     * @param is the ItemStack to clone and modify
     */
    public ItemBuilder(ItemStack is) {
        this.is = is.clone();
    }

    /**
     * Creates a new ItemBuilder from an existing ItemStack with a display name.
     *
     * @param is   the ItemStack to use
     * @param name the display name to set
     */
    public ItemBuilder(ItemStack is, String name) {
        this.is = is;
        setName(name);
    }

    /**
     * Creates a new ItemBuilder with the specified material.
     *
     * @param m the material type
     */
    public ItemBuilder(Material m) {
        is = new ItemStack(m);
    }

    /**
     * Creates a new ItemBuilder with the specified material and display name.
     *
     * @param m    the material type
     * @param name the display name to set
     */
    public ItemBuilder(Material m, String name) {
        is = new ItemStack(m);
        setName(name);
    }

    /**
     * Creates a new ItemBuilder with the specified material and amount.
     *
     * @param m      the material type
     * @param amount the stack amount
     */
    public ItemBuilder(Material m, int amount) {
        is = new ItemStack(m, amount);
    }

    /**
     * Creates a new ItemBuilder with the specified material, amount, and durability.
     *
     * @param m          the material type
     * @param amount     the stack amount
     * @param durability the durability/damage value
     */
    public ItemBuilder(Material m, int amount, byte durability) {
        is = new ItemStack(m, amount, durability);
    }

    /**
     * Creates a new ItemBuilder with the specified material, amount, and durability.
     *
     * @param type       the material type
     * @param amount     the stack amount
     * @param durability the durability/damage value
     */
    public ItemBuilder(Material type, int amount, short durability) {
        is = new ItemStack(type, amount, durability);
    }

    /**
     * Creates a clone of this ItemBuilder.
     *
     * @return a new ItemBuilder with a cloned ItemStack
     */
    public ItemBuilder clone() {
        return new ItemBuilder(is);
    }

    /**
     * Sets the durability (damage) value of the item.
     *
     * @param dur the durability value
     * @return this builder for chaining
     */
    public ItemBuilder setDurability(short dur) {
        is.setDurability(dur);
        return this;
    }

    /**
     * Sets the durability (damage) value of the item.
     *
     * @param dur the durability value
     * @return this builder for chaining
     */
    public ItemBuilder setDurability(int dur) {
        is.setDurability((short) dur);
        return this;
    }

    /**
     * Sets the stack amount of the item.
     *
     * @param amount the stack amount
     * @return this builder for chaining
     */
    public ItemBuilder setAmount(int amount) {
        is.setAmount(amount);
        return this;
    }

    /**
     * Sets the display name of the item with color code translation.
     * <p>
     * Color codes using '&amp;' will be translated to Minecraft color codes.
     *
     * @param str the display name (supports '&amp;' color codes)
     * @return this builder for chaining
     */
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

    /**
     * Sets the display name of the item using an Adventure Component.
     *
     * @param component the Adventure Component to use as the display name
     * @return this builder for chaining
     */
    public ItemBuilder setName(Component component) {
        ItemUtil.setName(is, component);
        return this;
    }

    /**
     * Sets the lore of the item using Adventure Components.
     *
     * @param lores the list of Adventure Components to use as lore lines
     * @return this builder for chaining
     */
    public ItemBuilder addLore(List<Component> lores) {
        ItemUtil.setLore(is, lores);
        return this;
    }

    /**
     * Sets the display name of the item without color code translation.
     *
     * @param name the display name (raw, no color code translation)
     * @return this builder for chaining
     */
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
