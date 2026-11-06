package com.ebicep.warlords.player.general.armorcustomization;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.honorifics.HonorificCost;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.permissions.Permissions;
import com.ebicep.warlords.player.general.ArmorManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.warlords.Utils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.TrimMaterial;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

public final class ArmorCustomizationMenu {

    public static final ItemStack MENU_ICON = templateIcon(Material.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, new ItemBuilder(Material.PAPER)
            .name(Component.text("Armor Patterns", NamedTextColor.LIGHT_PURPLE))
            .lore(
                    Component.text("Customize the pattern, material,", NamedTextColor.GRAY),
                    Component.text("and enchant glint of each armor piece.", NamedTextColor.GRAY),
                    Component.empty(),
                    Component.text("Click to customize!", NamedTextColor.YELLOW)
            ));

    private static final Component PVP_NOTE = Component.text("In PvP, Blue uses Lapis and Red uses Redstone.", NamedTextColor.DARK_GRAY);

    private ArmorCustomizationMenu() {
    }

    public static void open(Player player) {
        open(player, null);
    }

    public static void open(Player player, @Nullable Runnable returnTo) {
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);
        boolean hasSupporter = Permissions.isSupporter(player);
        normalize(databasePlayer, hasSupporter);
        ArmorCustomizationProfile profile = databasePlayer.getArmorCustomization();
        Menu menu = new Menu("Armor Patterns", 9 * 4);

        for (int i = 0; i < ArmorPiece.VALUES.length; i++) {
            ArmorPiece piece = ArmorPiece.VALUES[i];
            menu.setItem(1 + i * 2, 1, pieceIcon(piece, profile, hasSupporter), (m, event) -> openPiece(player, piece, returnTo));
        }

        if (returnTo != null) {
            menu.setItem(3, 3, Menu.MENU_BACK, (m, event) -> returnTo.run());
        }
        menu.setItem(4, 3, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.addBorder(Menu.GRAY_EMPTY_PANE, true);
        menu.openForPlayer(player);
    }

    public static void openPiece(Player player, ArmorPiece piece) {
        openPiece(player, piece, null);
    }

    public static void openPiece(Player player, ArmorPiece piece, @Nullable Runnable returnTo) {
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);
        ArmorCustomizationProfile profile = databasePlayer.getArmorCustomization();
        boolean hasSupporter = Permissions.isSupporter(player);
        Menu menu = new Menu(piece.getDisplayName(), 9 * 4);

        if (piece.supportsTrim()) {
            ArmorPattern pattern = profile.getSelectedPattern(piece);
            ItemBuilder patternButton = new ItemBuilder(pattern == null ? Material.ARMOR_STAND : Material.PAPER)
                    .name(Component.text("Armor Pattern", NamedTextColor.AQUA))
                    .lore(
                            Component.text("Selected: ", NamedTextColor.GRAY)
                                    .append(Component.text(pattern == null ? "None" : pattern.getDisplayName(), NamedTextColor.YELLOW)),
                            Component.empty(),
                            Component.text("Click to choose", NamedTextColor.YELLOW)
                    );
            ItemStack patternIcon = pattern == null ? patternButton.get() : templateIcon(pattern.getIcon(), patternButton);
            menu.setItem(2, 1, patternIcon, (m, event) -> openPatterns(player, piece, returnTo));
            ArmorPatternMaterial material = profile.getSelectedMaterial(piece);
            menu.setItem(4, 1, new ItemBuilder(material == null ? Material.COPPER_INGOT : material.getIcon())
                    .name(Component.text("Armor Pattern Material", NamedTextColor.AQUA))
                    .lore(
                            Component.text("Selected: ", NamedTextColor.GRAY)
                                    .append(Component.text(material == null ? "None" : material.getDisplayName(), NamedTextColor.YELLOW)),
                            PVP_NOTE,
                            Component.empty(),
                            Component.text("Click to choose", NamedTextColor.YELLOW)
                    )
                    .get(), (m, event) -> openMaterials(player, piece, returnTo));
        } else {
            menu.setItem(2, 1, new ItemBuilder(Material.BARRIER)
                    .name(Component.text("No Armor Trim", NamedTextColor.RED))
                    .lore(Component.text("Class helmets cannot use armor trims.", NamedTextColor.GRAY))
                    .get(), Menu.ACTION_DO_NOTHING);
        }

        menu.setItem(6, 1, enchantIcon(piece, profile, hasSupporter), (m, event) -> onEnchantClick(player, databasePlayer, piece, returnTo));
        menu.setItem(3, 3, Menu.MENU_BACK, (m, event) -> open(player, returnTo));
        menu.setItem(4, 3, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.addBorder(Menu.GRAY_EMPTY_PANE, true);
        menu.openForPlayer(player);
    }

    public static void openPatterns(Player player, ArmorPiece piece) {
        openPatterns(player, piece, null);
    }

    public static void openPatterns(Player player, ArmorPiece piece, @Nullable Runnable returnTo) {
        if (!piece.supportsTrim()) {
            openPiece(player, piece, returnTo);
            return;
        }
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);
        ArmorCustomizationProfile profile = databasePlayer.getArmorCustomization();
        boolean hasSupporter = Permissions.isSupporter(player);
        Menu menu = new Menu("Patterns: " + piece.getDisplayName(), 9 * 4);
        for (int i = 0; i < ArmorPattern.VALUES.length; i++) {
            ArmorPattern pattern = ArmorPattern.VALUES[i];
            boolean available = pattern.isSupporterExclusive() ? hasSupporter : profile.isUnlocked(pattern);
            boolean selected = profile.getSelectedPattern(piece) == pattern;
            ItemBuilder builder = new ItemBuilder(Material.PAPER)
                    .name(Component.text(pattern.getDisplayName(), available ? NamedTextColor.AQUA : NamedTextColor.GRAY))
                    .lore(optionLore(available, selected, pattern.isSupporterExclusive(), hasSupporter, pattern.getCost()));
            if (selected && available) {
                builder.glow();
            }
            menu.setItem(i % 9, i / 9, templateIcon(pattern.getIcon(), builder), (m, event) -> {
                if (pattern.isSupporterExclusive() && !hasSupporter) {
                    sendSupporterRequired(player);
                } else if (selected) {
                    profile.selectPattern(piece, null, hasSupporter);
                    saveAndRefresh(player);
                } else if (available) {
                    profile.selectPattern(piece, pattern, hasSupporter);
                    saveAndRefresh(player);
                } else {
                    openPurchase(player, databasePlayer, "Purchase Pattern",
                            Component.text(pattern.getDisplayName(), NamedTextColor.AQUA), pattern.getCost(),
                            () -> profile.isUnlocked(pattern), () -> {
                                profile.unlock(pattern);
                                profile.selectPattern(piece, pattern, true);
                            }, () -> openPatterns(player, piece, returnTo));
                    return;
                }
                openPatterns(player, piece, returnTo);
            });
        }
        menu.setItem(3, 3, Menu.MENU_BACK, (m, event) -> openPiece(player, piece, returnTo));
        menu.setItem(4, 3, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openMaterials(Player player, ArmorPiece piece) {
        openMaterials(player, piece, null);
    }

    public static void openMaterials(Player player, ArmorPiece piece, @Nullable Runnable returnTo) {
        if (!piece.supportsTrim()) {
            openPiece(player, piece, returnTo);
            return;
        }
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);
        ArmorCustomizationProfile profile = databasePlayer.getArmorCustomization();
        boolean hasSupporter = Permissions.isSupporter(player);
        Menu menu = new Menu("Materials: " + piece.getDisplayName(), 9 * 4);
        for (int i = 0; i < ArmorPatternMaterial.VALUES.length; i++) {
            ArmorPatternMaterial material = ArmorPatternMaterial.VALUES[i];
            boolean available = material.isSupporterExclusive() ? hasSupporter : profile.isUnlocked(material);
            boolean selected = profile.getSelectedMaterial(piece) == material;
            ItemBuilder builder = new ItemBuilder(material.getIcon())
                    .name(Component.text(material.getDisplayName(), available ? NamedTextColor.AQUA : NamedTextColor.GRAY))
                    .lore(optionLore(available, selected, material.isSupporterExclusive(), hasSupporter, material.getCost()));
            if (selected && available) {
                builder.glow();
            }
            int slot = i < 9 ? i : 3 + (i - 9);
            int row = i < 9 ? 0 : 1;
            menu.setItem(slot, row, builder.get(), (m, event) -> {
                if (material.isSupporterExclusive() && !hasSupporter) {
                    sendSupporterRequired(player);
                } else if (selected) {
                    profile.selectMaterial(piece, null, hasSupporter);
                    saveAndRefresh(player);
                } else if (available) {
                    profile.selectMaterial(piece, material, hasSupporter);
                    saveAndRefresh(player);
                } else {
                    openPurchase(player, databasePlayer, "Purchase Material",
                            Component.text(material.getDisplayName(), NamedTextColor.AQUA), material.getCost(),
                            () -> profile.isUnlocked(material), () -> {
                                profile.unlock(material);
                                profile.selectMaterial(piece, material, true);
                            }, () -> openMaterials(player, piece, returnTo));
                    return;
                }
                openMaterials(player, piece, returnTo);
            });
        }
        menu.setItem(3, 3, Menu.MENU_BACK, (m, event) -> openPiece(player, piece, returnTo));
        menu.setItem(4, 3, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    private static void onEnchantClick(Player player, DatabasePlayer databasePlayer, ArmorPiece piece, @Nullable Runnable returnTo) {
        ArmorCustomizationProfile profile = databasePlayer.getArmorCustomization();
        boolean hasSupporter = Permissions.isSupporter(player);
        if (!hasSupporter) {
            sendSupporterRequired(player);
            openPiece(player, piece, returnTo);
            return;
        }
        profile.setEnchantEnabled(piece, !profile.isEnchantEnabled(piece));
        saveAndRefresh(player);
        openPiece(player, piece, returnTo);
    }

    private static ItemStack pieceIcon(ArmorPiece piece, ArmorCustomizationProfile profile, boolean hasSupporter) {
        ArmorPattern pattern = profile.getEffectivePattern(piece, hasSupporter);
        ArmorPatternMaterial material = profile.getEffectiveMaterial(piece, hasSupporter);
        boolean glint = profile.getEffectiveGlint(piece, hasSupporter);
        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        if (piece.supportsTrim()) {
            lore.add(Component.text("Pattern: ", NamedTextColor.GRAY)
                    .append(Component.text(pattern == null ? "None" : pattern.getDisplayName(), NamedTextColor.YELLOW)));
            lore.add(Component.text("Material: ", NamedTextColor.GRAY)
                    .append(Component.text(materialName(pattern, material), NamedTextColor.YELLOW)));
            lore.add(PVP_NOTE);
        } else {
            lore.add(Component.text("Class helmets cannot use armor trims.", NamedTextColor.GRAY));
        }
        lore.add(Component.text("Enchant: ", NamedTextColor.GRAY).append(enchantStatus(piece, profile, hasSupporter)));
        lore.add(Component.empty());
        lore.add(Component.text("Click to customize", NamedTextColor.YELLOW));

        ItemStack preview = new ItemStack(piece.getPreview());
        if (pattern != null) {
            preview = Utils.applyTrimTo(preview, material == null ? TrimMaterial.COPPER : material.getTrimMaterial(), pattern.getTrimPattern());
        }
        ItemBuilder builder = new ItemBuilder(preview)
                .name(Component.text(piece.getDisplayName(), NamedTextColor.AQUA))
                .lore(lore);
        if (glint) {
            builder.enchant(Enchantment.UNBREAKING, 1);
        }
        return builder.get();
    }

    private static ItemStack enchantIcon(ArmorPiece piece, ArmorCustomizationProfile profile, boolean hasSupporter) {
        boolean enabled = profile.isEnchantEnabled(piece);
        ItemBuilder builder = new ItemBuilder(Material.ENCHANTED_BOOK)
                .name(Component.text("Enchant", NamedTextColor.LIGHT_PURPLE));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.add(Component.text("Adds an enchant glint to this piece.", NamedTextColor.GRAY));
        lore.add(Component.empty());
        lore.add(Component.text("SUPPORTER EXCLUSIVE", NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD));
        lore.add(Component.empty());
        if (!hasSupporter) {
            lore.add(Component.text("Requires the Supporter rank", NamedTextColor.RED));
        } else if (enabled) {
            builder.glow();
            lore.add(Component.text("ENABLED", NamedTextColor.GREEN, TextDecoration.BOLD));
            lore.add(Component.text("Click to disable", NamedTextColor.YELLOW));
        } else {
            lore.add(Component.text("DISABLED", NamedTextColor.RED, TextDecoration.BOLD));
            lore.add(Component.text("Click to enable", NamedTextColor.YELLOW));
        }
        return builder.lore(lore).get();
    }

    private static List<Component> optionLore(boolean available, boolean selected, boolean supporterExclusive,
                                              boolean hasSupporter, @Nullable HonorificCost cost) {
        List<Component> lore = new ArrayList<>();
        if (supporterExclusive) {
            lore.add(Component.empty());
            lore.add(Component.text("SUPPORTER EXCLUSIVE", NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD));
        }
        if (supporterExclusive && !hasSupporter) {
            lore.add(Component.empty());
            lore.add(Component.text("Requires the Supporter rank", NamedTextColor.RED));
            return lore;
        }
        if (!available && cost != null) {
            lore.add(Component.empty());
            lore.addAll(cost.getLore());
            lore.add(Component.empty());
            lore.add(Component.text("LOCKED", NamedTextColor.RED, TextDecoration.BOLD));
            lore.add(Component.text("Click to purchase", NamedTextColor.YELLOW));
            return lore;
        }
        lore.add(Component.empty());
        if (selected) {
            lore.add(Component.text("SELECTED", NamedTextColor.GREEN, TextDecoration.BOLD));
            lore.add(Component.text("Click to remove", NamedTextColor.YELLOW));
        } else {
            lore.add(Component.text("UNLOCKED", NamedTextColor.AQUA, TextDecoration.BOLD));
            lore.add(Component.text("Click to select", NamedTextColor.YELLOW));
        }
        return lore;
    }

    private static ItemStack templateIcon(Material template, ItemBuilder builder) {
        ItemStack icon = builder.get();
        icon.setData(DataComponentTypes.ITEM_MODEL, template.getKey());
        return icon;
    }

    private static void normalize(DatabasePlayer databasePlayer, boolean hasSupporter) {
        if (databasePlayer.getArmorCustomization().applySupporterDefaults(hasSupporter)) {
            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
        }
    }

    private static void openPurchase(Player player, DatabasePlayer databasePlayer, String title, Component name,
                                     HonorificCost cost, BooleanSupplier unlocked, Runnable grant, Runnable returnTo) {
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Unlock ", NamedTextColor.GRAY).append(name).append(Component.text(" permanently.")));
        lore.add(Component.empty());
        lore.addAll(cost.getLore());
        Menu.openConfirmationMenu(player, title, 3, lore, Menu.GO_BACK, (m, event) -> {
            if (unlocked.getAsBoolean()) {
                returnTo.run();
                return;
            }
            if (!cost.canAfford(databasePlayer)) {
                player.sendMessage(Component.text("You do not have the required materials for this purchase.", NamedTextColor.RED));
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 0.7f);
                returnTo.run();
                return;
            }
            cost.take(databasePlayer);
            grant.run();
            saveAndRefresh(player);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1.2f);
            returnTo.run();
        }, (m, event) -> returnTo.run(), ignored -> {
        });
    }

    private static String materialName(@Nullable ArmorPattern pattern, @Nullable ArmorPatternMaterial material) {
        if (pattern == null) {
            return material == null ? "None" : material.getDisplayName();
        }
        return material == null ? "Copper" : material.getDisplayName();
    }

    private static Component enchantStatus(ArmorPiece piece, ArmorCustomizationProfile profile, boolean hasSupporter) {
        if (!hasSupporter) {
            return Component.text("Supporter required", NamedTextColor.RED);
        }
        return Component.text(profile.isEnchantEnabled(piece) ? "Enabled" : "Disabled",
                profile.isEnchantEnabled(piece) ? NamedTextColor.GREEN : NamedTextColor.RED);
    }

    private static void sendSupporterRequired(Player player) {
        player.sendMessage(Component.text("This armor customization requires the Supporter rank.", NamedTextColor.RED));
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 0.7f);
    }

    private static void saveAndRefresh(Player player) {
        DatabaseManager.queueUpdatePlayerAsync(DatabaseManager.getPlayer(player));
        WarlordsEntity entity = Warlords.getPlayer(player);
        if (entity instanceof WarlordsPlayer warlordsPlayer && warlordsPlayer.getGame() != null) {
            ArmorManager.resetArmor(player, warlordsPlayer);
            return;
        }
        if (!player.getWorld().getName().equals("MainLobby")) {
            ArmorManager.resetArmor(player);
        }
    }

}
