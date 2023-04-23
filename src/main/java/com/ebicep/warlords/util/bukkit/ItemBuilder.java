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

    public ItemBuilder name(@Nonnull String name) {
        meta().displayName(Component.text(name));
        return this;
    }

    public ItemBuilder name(Component component) {
        meta().displayName(ComponentUtils.nonItalicBase().append(component));
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

    public ItemBuilder loreLEGACY(String... lore) {
        return loreLEGACY(Arrays.asList(lore));
    }

    public ItemBuilder loreLEGACY(Collection<String> lore2) {
        List<Component> lore = new ArrayList<>(lore2.size());
        for (String s : lore2) {
            if (s == null) {
                continue;
            }
            lore.add(Component.text(s));
        }
//        for (String row : lore) {
//            if (row == null || row.contains("\n")) {
//                // Fix for \n and null
//                List<String> newLore = new ArrayList<>(Math.max(lore.size() * 2, 16));
//                for (String loreRow : lore) {
//                    if (loreRow != null) {
//                        if (loreRow.contains("\n")) {
//                            String chatColor = "";
//                            for (String split : loreRow.split("\n")) {
//                                String combined = !split.isEmpty() && split.charAt(0) == ChatColor.COLOR_CHAR ? split : chatColor + split;
//                                newLore.add(combined);
//                                chatColor = ChatColor.getLastColors(combined);
//                            }
//                        } else {
//                            newLore.add(loreRow);
//                        }
//                    }
//                }
//                meta().setLore(newLore);
//                return this;
//            }
//        }
        meta().lore(ComponentUtils.flattenComponents(lore));
        return this;
    }

    public ItemBuilder addLore(String... lore) {
        return addLore(Arrays.asList(lore));
    }

    public ItemBuilder addLore(Collection<String> lore2) {
        List<Component> lore = new ArrayList<>(lore2.size());
        for (String s : lore2) {
            lore.add(Component.text(s));
        }
        return addLoreC(lore);
//        for (String row : lore) {
//            if (row == null || row.contains("\n")) {
//                // Fix for \n and null
//                List<String> newLore = new ArrayList<>(Math.max(lore.size() * 2, 16));
//                newLore.addAll(meta().getLore());
//                for (String loreRow : lore) {
//                    if (loreRow != null) {
//                        if (loreRow.contains("\n")) {
//                            String chatColor = "";
//                            for (String split : loreRow.split("\n")) {
//                                String combined = !split.isEmpty() && split.charAt(0) == ChatColor.COLOR_CHAR ? split : chatColor + split;
//                                newLore.add(combined);
//                                chatColor = ChatColor.getLastColors(combined);
//                            }
//                        } else {
//                            newLore.add(loreRow);
//                        }
//                    }
//                }
//                meta().setLore(newLore);
//                return this;
//            }
//        }
    }

    public ItemBuilder lore(Collection<Component> lore) {
        meta().lore(new ArrayList<>());
        addLoreC(lore);
        return this;
    }

    public ItemBuilder lore(Component... lore) {
        lore(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder addLore(Component lore) {
        addLoreC(Collections.singletonList(lore));
        return this;
    }

    public ItemBuilder addLore(Component... lore) {
        addLoreC(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder addLoreC(Collection<Component> lore) {
        List<Component> components = meta().lore();
        if (components == null) {
            components = new ArrayList<>();
        }
        for (Component component : lore) {
            components.add(ComponentUtils.nonItalicBase().append(component));
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
        meta().setUnbreakable(true);
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