package com.ebicep.warlords.honorifics;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.featureflags.FeatureFlags;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.permissions.Permissions;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

public final class HonorificMenu {

    private HonorificMenu() {
    }

    public static void open(Player player) {
        if (!HonorificManager.honorificsEnabled(player)) {
            FeatureFlags.sendDisabledMessage(player);
            return;
        }
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);
        HonorificManager.forceChallengeRefresh(databasePlayer, player);
        HonorificProfile profile = HonorificManager.getProfile(player);
        validatePatreonAccess(player, profile);
        Menu menu = new Menu("Honorifics", 9 * 6);

        menu.setItem(4, 0, new ItemBuilder(Material.NAME_TAG)
                .name(Component.text("Honorific Preview", NamedTextColor.GOLD))
                .lore(getPreviewLore(profile)).get(), Menu.ACTION_DO_NOTHING);

        for (int i = 0; i < Honorific.VALUES.length; i++) {
            Honorific honorific = Honorific.VALUES[i];
            int x = 1 + i % 7;
            int y = 1 + i / 7;
            boolean unlocked = profile.isUnlocked(honorific);
            boolean equipped = profile.getEquippedHonorific() == honorific;
            ItemBuilder builder = new ItemBuilder(honorific.getIcon())
                    .name(Component.text(honorific.getDisplayName(), unlocked ? NamedTextColor.AQUA : NamedTextColor.GRAY))
                    .lore(getHonorificLore(honorific, databasePlayer, unlocked, equipped));
            if (equipped) {
                builder.glow();
            }
            menu.setItem(x, y, builder.get(), (m, event) -> {
                if (unlocked) {
                    profile.equip(equipped ? null : honorific);
                    saveAndRefresh(player);
                    open(player);
                } else if (honorific.isPurchasable()) {
                    openHonorificPurchase(player, databasePlayer, honorific);
                }
            });
        }

        menu.setItem(2, 5, new ItemBuilder(Material.PAINTING)
                .name(Component.text("Honorific Colors", NamedTextColor.GREEN))
                .lore(Component.text("Selected: ", NamedTextColor.GRAY)
                                .append(Component.text(profile.getSelectedColor().getDisplayName(), profile.getSelectedColor().getTextColor())),
                        Component.empty(), Component.text("Click to customize", NamedTextColor.YELLOW))
                .get(), (m, event) -> openColors(player));
        menu.setItem(4, 5, new ItemBuilder(Material.WRITABLE_BOOK)
                .name(Component.text("Honorific Fonts", NamedTextColor.GREEN))
                .lore(Component.text("Selected: ", NamedTextColor.GRAY)
                                .append(Component.text(profile.getSelectedFont().getDisplayName(), NamedTextColor.AQUA)),
                        Component.empty(), Component.text("Click to customize", NamedTextColor.YELLOW))
                .get(), (m, event) -> openFonts(player));
        menu.setItem(6, 5, new ItemBuilder(Material.BARRIER)
                .name(Component.text("Unequip Honorific", NamedTextColor.RED))
                .lore(Component.text(profile.getEquippedHonorific() == null ? "No honorific is equipped." : "Remove your current honorific.", NamedTextColor.GRAY))
                .get(), (m, event) -> {
            if (profile.equip(null)) {
                saveAndRefresh(player);
            }
            open(player);
        });
        menu.addBorder(Menu.GRAY_EMPTY_PANE, true);
        menu.openForPlayer(player);
    }

    public static void openColors(Player player) {
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);
        HonorificProfile profile = HonorificManager.getProfile(player);
        boolean hasPatreon = Permissions.isPatreon(player);
        validatePatreonAccess(player, profile);
        Menu menu = new Menu("Honorific Colors", 9 * 4);
        for (int i = 0; i < HonorificColor.VALUES.length; i++) {
            HonorificColor color = HonorificColor.VALUES[i];
            boolean available = color.isPatreonExclusive() ? hasPatreon : profile.isUnlocked(color);
            boolean selected = profile.getSelectedColor() == color;
            ItemBuilder builder = new ItemBuilder(color.getIcon())
                    .name(Component.text(color.getDisplayName(), color.getTextColor()))
                    .lore(getCustomizationLore(available, selected, color.getCost(), color.isPatreonExclusive(), hasPatreon));
            if (selected) {
                builder.glow();
            }
            menu.setItem(1 + i % 7, 1 + i / 7, builder.get(), (m, event) -> {
                if (color.isPatreonExclusive()) {
                    if (Permissions.isPatreon(player)) {
                        profile.selectColor(color, true);
                        saveAndRefresh(player);
                    } else {
                        sendPatreonRequired(player);
                    }
                    openColors(player);
                } else if (available) {
                    profile.selectColor(color);
                    saveAndRefresh(player);
                    openColors(player);
                } else {
                    openColorPurchase(player, databasePlayer, color);
                }
            });
        }
        menu.setItem(3, 3, Menu.MENU_BACK, (m, event) -> open(player));
        menu.setItem(4, 3, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.addBorder(Menu.GRAY_EMPTY_PANE, true);
        menu.openForPlayer(player);
    }

    public static void openFonts(Player player) {
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);
        HonorificProfile profile = HonorificManager.getProfile(player);
        boolean hasPatreon = Permissions.isPatreon(player);
        validatePatreonAccess(player, profile);
        Menu menu = new Menu("Honorific Fonts", 9 * 4);
        for (int i = 0; i < HonorificFont.VALUES.length; i++) {
            HonorificFont font = HonorificFont.VALUES[i];
            boolean available = font.isPatreonExclusive() ? hasPatreon : profile.isUnlocked(font);
            boolean selected = profile.getSelectedFont() == font;
            ItemBuilder builder = new ItemBuilder(font.getIcon())
                    .name(font.createComponent(font.getDisplayName(), NamedTextColor.AQUA))
                    .lore(getCustomizationLore(available, selected, font.getCost(), font.isPatreonExclusive(), hasPatreon));
            if (selected) {
                builder.glow();
            }
            menu.setItem(1 + i * 2, 1, builder.get(), (m, event) -> {
                if (font.isPatreonExclusive()) {
                    if (Permissions.isPatreon(player)) {
                        profile.selectFont(font, true);
                        saveAndRefresh(player);
                    } else {
                        sendPatreonRequired(player);
                    }
                    openFonts(player);
                } else if (available) {
                    profile.selectFont(font);
                    saveAndRefresh(player);
                    openFonts(player);
                } else {
                    openFontPurchase(player, databasePlayer, font);
                }
            });
        }
        menu.setItem(3, 3, Menu.MENU_BACK, (m, event) -> open(player));
        menu.setItem(4, 3, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.addBorder(Menu.GRAY_EMPTY_PANE, true);
        menu.openForPlayer(player);
    }

    private static List<Component> getPreviewLore(HonorificProfile profile) {
        Honorific equipped = profile.getEquippedHonorific();
        if (equipped == null) {
            return List.of(Component.text("No honorific equipped", NamedTextColor.GRAY));
        }
        return List.of(Component.text("Current:", NamedTextColor.GRAY),
                Component.text("[", NamedTextColor.DARK_GRAY)
                        .append(profile.getSelectedFont().createComponent(equipped.getDisplayName(), profile.getSelectedColor().getTextColor()))
                        .append(Component.text("] Player", NamedTextColor.DARK_GRAY)));
    }

    private static List<Component> getHonorificLore(Honorific honorific, DatabasePlayer databasePlayer, boolean unlocked, boolean equipped) {
        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.add(Component.text(honorific.getRequirement(), NamedTextColor.GRAY));
        if (!unlocked) {
            lore.add(Component.empty());
            lore.add(Component.text("Progress: ", NamedTextColor.GRAY)
                    .append(Component.text(HonorificManager.getProgressText(honorific, databasePlayer), NamedTextColor.YELLOW)));
            if (honorific.getCost() != null) {
                lore.add(Component.empty());
                lore.addAll(honorific.getCost().getLore());
            }
        }
        lore.add(Component.empty());
        if (equipped) {
            lore.add(Component.text("EQUIPPED", NamedTextColor.GREEN, TextDecoration.BOLD));
            lore.add(Component.text("Click to unequip", NamedTextColor.YELLOW));
        } else if (unlocked) {
            lore.add(Component.text("UNLOCKED", NamedTextColor.AQUA, TextDecoration.BOLD));
            lore.add(Component.text("Click to equip", NamedTextColor.YELLOW));
        } else if (honorific.isPurchasable()) {
            lore.add(Component.text("LOCKED", NamedTextColor.RED, TextDecoration.BOLD));
            lore.add(Component.text("Click to purchase", NamedTextColor.YELLOW));
        } else {
            lore.add(Component.text("LOCKED — Complete the challenge", NamedTextColor.RED));
        }
        return lore;
    }

    private static List<Component> getCustomizationLore(boolean available, boolean selected, @Nullable HonorificCost cost,
                                                         boolean patreonExclusive, boolean hasPatreon) {
        List<Component> lore = new ArrayList<>();
        if (patreonExclusive) {
            lore.add(Component.empty());
            lore.add(Component.text("SUPPORTER EXCLUSIVE", NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD));
            lore.add(Component.empty());
            if (selected) {
                lore.add(Component.text("SELECTED", NamedTextColor.GREEN, TextDecoration.BOLD));
            } else if (hasPatreon) {
                lore.add(Component.text("AVAILABLE", NamedTextColor.AQUA, TextDecoration.BOLD));
                lore.add(Component.text("Click to select", NamedTextColor.YELLOW));
            } else {
                lore.add(Component.text("Requires the Supporter rank", NamedTextColor.RED));
            }
            return lore;
        }
        if (!available && cost != null) {
            lore.addAll(cost.getLore());
            lore.add(Component.empty());
        }
        if (selected) {
            lore.add(Component.text("SELECTED", NamedTextColor.GREEN, TextDecoration.BOLD));
        } else if (available) {
            lore.add(Component.text("UNLOCKED", NamedTextColor.AQUA, TextDecoration.BOLD));
            lore.add(Component.text("Click to select", NamedTextColor.YELLOW));
        } else {
            lore.add(Component.text("LOCKED", NamedTextColor.RED, TextDecoration.BOLD));
            lore.add(Component.text("Click to purchase", NamedTextColor.YELLOW));
        }
        return lore;
    }

    private static void openHonorificPurchase(Player player, DatabasePlayer databasePlayer, Honorific honorific) {
        HonorificCost cost = honorific.getCost();
        if (cost == null) {
            return;
        }
        openPurchase(player, databasePlayer, "Purchase Honorific",
                Component.text("[" + honorific.getDisplayName() + "]", NamedTextColor.AQUA), cost,
                () -> HonorificManager.getProfile(player).isUnlocked(honorific),
                () -> HonorificManager.getProfile(player).unlock(honorific),
                () -> open(player));
    }

    private static void openColorPurchase(Player player, DatabasePlayer databasePlayer, HonorificColor color) {
        if (color.isPatreonExclusive()) {
            return;
        }
        HonorificCost cost = color.getCost();
        if (cost == null) {
            return;
        }
        openPurchase(player, databasePlayer, "Purchase Color", Component.text(color.getDisplayName(), color.getTextColor()), cost,
                () -> HonorificManager.getProfile(player).isUnlocked(color), () -> {
                    HonorificProfile profile = HonorificManager.getProfile(player);
                    profile.unlock(color);
                    profile.selectColor(color);
                }, () -> openColors(player));
    }

    private static void openFontPurchase(Player player, DatabasePlayer databasePlayer, HonorificFont font) {
        if (font.isPatreonExclusive()) {
            return;
        }
        HonorificCost cost = font.getCost();
        if (cost == null) {
            return;
        }
        openPurchase(player, databasePlayer, "Purchase Font", font.createComponent(font.getDisplayName(), NamedTextColor.AQUA), cost,
                () -> HonorificManager.getProfile(player).isUnlocked(font), () -> {
                    HonorificProfile profile = HonorificManager.getProfile(player);
                    profile.unlock(font);
                    profile.selectFont(font);
                }, () -> openFonts(player));
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
        }, (m, event) -> returnTo.run(), m -> {
        });
    }

    private static void validatePatreonAccess(Player player, HonorificProfile profile) {
        if (profile.validatePatreonAccess(Permissions.isPatreon(player))) {
            saveAndRefresh(player);
        }
    }

    private static void sendPatreonRequired(Player player) {
        player.sendMessage(Component.text("This Honorific style requires the Patreon rank.", NamedTextColor.RED));
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 0.7f);
    }

    private static void saveAndRefresh(Player player) {
        DatabaseManager.queueUpdatePlayerAsync(DatabaseManager.getPlayer(player));
        HonorificManager.refreshDisplays(player);
    }
}
