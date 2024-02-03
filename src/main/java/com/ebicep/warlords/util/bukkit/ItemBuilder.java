package com.ebicep.warlords.util.bukkit;

import com.ebicep.warlords.Warlords;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class ItemBuilder {

    public static final NamespacedKey ON_USE_NAMESPACED_KEY = new NamespacedKey(Warlords.getInstance(), "on_use");
    @Nonnull
    private final ItemStack item;
    @Nullable
    private ItemMeta meta = null;

    public ItemBuilder(@Nonnull Material type, int amount) {
        item = new ItemStack(type, amount);
        hideAllFlags();
    }

    private void hideAllFlags() {
        if (item.getType() == Material.AIR) {
            return;
        }
        meta().addItemFlags(
                ItemFlag.HIDE_ENCHANTS,
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_ITEM_SPECIFICS,
                ItemFlag.HIDE_DYE,
                ItemFlag.HIDE_ARMOR_TRIM
        );
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

    public ItemBuilder(@Nonnull ItemStack stack) throws IllegalArgumentException {
        item = new ItemStack(stack);
        hideAllFlags();
    }

    public ItemBuilder(@Nonnull Material material, @Nonnull PotionType potionType) {
        this(material);
        PotionMeta potionMeta = (PotionMeta) meta();
        potionMeta.setBasePotionData(new PotionData(potionType));
        item.setItemMeta(potionMeta);
        hideAllFlags();
    }

    public ItemBuilder(@Nonnull Material type) {
        item = new ItemStack(type);
        hideAllFlags();
    }

    public ItemBuilder name(Component component) {
        meta().displayName(ComponentUtils.componentBase().append(component));
        return this;
    }

    public Component getName() {
        return meta().displayName();
    }

    public ItemBuilder enchant(@Nonnull Enchantment enchant, int level) {
        meta().addEnchant(enchant, level, true);
        return this;
    }

    public ItemBuilder removeFlags(ItemFlag... ifs) {
        meta().removeItemFlags(ifs);
        return this;
    }

    public ItemBuilder noLore() {
        meta().lore(null);
        return this;
    }

    public ItemBuilder lore(Component... lore) {
        lore(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder lore(Collection<Component> lore) {
        meta().lore(new ArrayList<>());
        addLore(lore);
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

    public ItemBuilder addLore(Component lore) {
        addLore(Collections.singletonList(lore));
        return this;
    }

    public ItemBuilder addLore(Component... lore) {
        addLore(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder prependLore(Component lore) {
        prependLore(Collections.singletonList(lore));
        return this;
    }

    public ItemBuilder prependLore(Collection<Component> lore) {
        List<Component> components = new ArrayList<>();
        for (Component component : lore) {
            components.add(ComponentUtils.componentBase().append(component));
        }
        List<Component> previousLore = meta().lore();
        if (previousLore != null) {
            components.addAll(previousLore);
        }
        meta().lore(components);
        return this;
    }

    public ItemBuilder prependLore(Component... lore) {
        prependLore(Arrays.asList(lore));
        return this;
    }

    public List<Component> getLore() {
        return meta().lore();
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

    public ItemBuilder setOnUseID(String value) {
        PersistentDataContainer persistentDataContainer = meta().getPersistentDataContainer();
        persistentDataContainer.set(ON_USE_NAMESPACED_KEY, PersistentDataType.STRING, value);
        return this;
    }

    public ItemBuilder setPlaceableOn(EnumSet<Material> materials) {
        meta().setPlaceableKeys(materials.stream().map(Material::getKey).collect(Collectors.toList()));
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