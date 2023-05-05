package com.ebicep.warlords.util.bukkit;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class ItemBuilder {

    public static final ItemStack RED_ABILITY = new ItemStack(Material.RED_DYE);
    public static final ItemStack PURPLE_ABILITY = new ItemStack(Material.GLOWSTONE_DUST);
    public static final ItemStack BLUE_ABILITY = new ItemStack(Material.LIME_DYE);
    public static final ItemStack ORANGE_ABILITY = new ItemStack(Material.ORANGE_DYE);
    @Nonnull
    private final ItemStack item;
    @Nullable
    private ItemMeta meta = null;

    public ItemBuilder(@Nonnull Material type, int amount) {
        item = new ItemStack(type, amount);
    }

    public ItemBuilder(@Nonnull ItemStack stack) throws IllegalArgumentException {
        item = new ItemStack(stack);
    }

    public ItemBuilder(@Nonnull Material material, @Nonnull PotionType potionType) {
        this(material);
        PotionMeta potionMeta = (PotionMeta) meta();
        potionMeta.setBasePotionData(new PotionData(potionType));
        item.setItemMeta(potionMeta);
    }

    public ItemBuilder(@Nonnull Material type) {
        item = new ItemStack(type);
    }

    protected ItemMeta meta() {
        if (meta == null) {
            meta = item.getItemMeta();
            if (meta == null) {
                throw new IllegalStateException("Unable to get item meta for " + item);
            }
        }
        return meta;
    }

    public ItemBuilder name(Component component) {
        meta().displayName(ComponentUtils.componentBase().append(component));
        return this;
    }

    public ItemBuilder enchant(@Nonnull Enchantment enchant, int level) {
        meta().addEnchant(enchant, level, true);
        return this;
    }

    public ItemBuilder flags(ItemFlag... ifs) {
        meta().addItemFlags(ifs);
        return this;
    }

    public ItemBuilder lore(Collection<Component> lore) {
        meta().lore(new ArrayList<>());
        addLore(lore);
        return this;
    }

    public ItemBuilder lore(Component... lore) {
        lore(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder addLore(Component lore) {
        addLore(Collections.singletonList(lore));
        return this;
    }

    public ItemBuilder addLore(Component... lore) {
        addLore(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder addLore(Collection<Component> lore) {
        List<Component> components = meta().lore();
        if (components == null) {
            components = new ArrayList<>();
        }
        for (Component component : lore) {
            components.add(ComponentUtils.componentBase().append(component));
        }
        meta().lore(components);
        return this;
    }

    public ItemBuilder amount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemBuilder unbreakable() {
        return unbreakable(true);
    }

    public ItemBuilder unbreakable(boolean unbreakable) {
        meta().setUnbreakable(unbreakable);
        return this;
    }

    public ItemStack get() {
        if (this.meta != null) {
            this.item.setItemMeta(meta);
            this.meta = null;
        }
        return this.item;
    }
}