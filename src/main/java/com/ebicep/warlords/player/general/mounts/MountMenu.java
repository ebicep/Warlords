package com.ebicep.warlords.player.general.mounts;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.honorifics.HonorificCost;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.permissions.Permissions;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

public final class MountMenu {

    public static final ItemStack MENU_ICON = new ItemBuilder(Material.SADDLE)
            .name(Component.text("Mounts", NamedTextColor.GOLD))
            .lore(
                    Component.text("Choose a mount and horse armor", NamedTextColor.GRAY),
                    Component.text("for your PvP mount.", NamedTextColor.GRAY),
                    Component.empty(),
                    Component.text("Click to customize!", NamedTextColor.YELLOW)
            )
            .get();

    private MountMenu() {
    }

    public static void open(Player player) {
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);
        boolean hasSupporter = Permissions.isSupporter(player);
        normalize(databasePlayer, hasSupporter);
        MountProfile profile = databasePlayer.getMounts();
        Menu menu = new Menu("Mounts", 9 * 4);
        MountType type = profile.getSelectedType();
        MountArmor armor = profile.getSelectedArmor();

        menu.setItem(3, 1, new ItemBuilder(type.getIcon())
                .name(Component.text("Mount", NamedTextColor.GOLD))
                .lore(
                        Component.text("Selected: ", NamedTextColor.GRAY)
                                .append(Component.text(type.getDisplayName(), NamedTextColor.YELLOW)),
                        Component.empty(),
                        Component.text("Click to choose", NamedTextColor.YELLOW)
                )
                .get(), (m, event) -> openTypes(player));
        menu.setItem(5, 1, new ItemBuilder(armor == null ? Material.SADDLE : armor.getIcon())
                .name(Component.text("Horse Armor", NamedTextColor.GOLD))
                .lore(
                        Component.text("Selected: ", NamedTextColor.GRAY)
                                .append(Component.text(armor == null ? "None" : armor.getDisplayName(), NamedTextColor.YELLOW)),
                        Component.text("Only applies to horse mounts", NamedTextColor.DARK_GRAY),
                        Component.empty(),
                        Component.text("Click to choose", NamedTextColor.YELLOW)
                )
                .get(), (m, event) -> openArmors(player));
        menu.setItem(3, 3, Menu.MENU_BACK, (m, event) -> com.ebicep.warlords.menu.generalmenu.WarlordsNewHotbarMenu.PvPMenu.openPvPMenu(player));
        menu.setItem(4, 3, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.addBorder(Menu.GRAY_EMPTY_PANE, true);
        menu.openForPlayer(player);
    }

    public static void openTypes(Player player) {
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);
        boolean hasSupporter = Permissions.isSupporter(player);
        normalize(databasePlayer, hasSupporter);
        MountProfile profile = databasePlayer.getMounts();
        Menu menu = new Menu("Mount Types", 9 * 5);
        for (int i = 0; i < MountType.VALUES.length; i++) {
            MountType type = MountType.VALUES[i];
            boolean available = type.isSupporterExclusive() ? hasSupporter && profile.isUnlocked(type) : profile.isUnlocked(type);
            boolean selected = profile.getSelectedType() == type;
            ItemBuilder builder = new ItemBuilder(type.getIcon())
                    .name(Component.text(type.getDisplayName(), available ? NamedTextColor.GOLD : NamedTextColor.GRAY))
                    .lore(optionLore(available, selected, type != MountType.BROWN, type.isSupporterExclusive(), hasSupporter, type.getCost()));
            if (selected) {
                builder.glow();
            }
            menu.setItem(1 + (i % 4) * 2, 1 + i / 4, builder.get(), (m, event) -> {
                if (type.isSupporterExclusive() && !hasSupporter) {
                    sendSupporterRequired(player);
                } else if (selected) {
                    if (type != MountType.BROWN && profile.selectType(MountType.BROWN, hasSupporter)) {
                        save(databasePlayer);
                    }
                } else if (available) {
                    if (profile.selectType(type, hasSupporter)) {
                        save(databasePlayer);
                    }
                } else {
                    openPurchase(player, databasePlayer, "Purchase Mount",
                            Component.text(type.getDisplayName(), NamedTextColor.GOLD), type.getCost(),
                            () -> profile.isUnlocked(type), () -> {
                                profile.unlock(type);
                                profile.selectType(type, Permissions.isSupporter(player));
                            }, () -> openTypes(player));
                    return;
                }
                openTypes(player);
            });
        }
        menu.setItem(3, 4, Menu.MENU_BACK, (m, event) -> open(player));
        menu.setItem(4, 4, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.addBorder(Menu.GRAY_EMPTY_PANE, true);
        menu.openForPlayer(player);
    }

    public static void openArmors(Player player) {
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);
        boolean hasSupporter = Permissions.isSupporter(player);
        normalize(databasePlayer, hasSupporter);
        MountProfile profile = databasePlayer.getMounts();
        Menu menu = new Menu("Horse Armor", 9 * 4);
        boolean noArmor = profile.getSelectedArmor() == null;
        ItemBuilder none = new ItemBuilder(Material.BARRIER)
                .name(Component.text("No Armor", NamedTextColor.GOLD))
                .lore(armorLore(noArmor
                        ? List.of(Component.text("SELECTED", NamedTextColor.GREEN, TextDecoration.BOLD))
                        : List.of(Component.text("Click to unequip", NamedTextColor.YELLOW))));
        if (noArmor) {
            none.glow();
        }
        menu.setItem(1, 1, none.get(), (m, event) -> {
            if (profile.selectArmor(null, hasSupporter)) {
                save(databasePlayer);
            }
            openArmors(player);
        });
        for (int i = 0; i < MountArmor.VALUES.length; i++) {
            MountArmor armor = MountArmor.VALUES[i];
            boolean unlocked = profile.isUnlocked(armor);
            boolean available = armor.isSupporterExclusive() ? hasSupporter && unlocked : unlocked;
            boolean selected = profile.getSelectedArmor() == armor;
            ItemBuilder builder = new ItemBuilder(armor.getIcon())
                    .name(Component.text(armor.getDisplayName(), available ? NamedTextColor.GOLD : NamedTextColor.GRAY))
                    .lore(armorLore(optionLore(available, selected, true, armor.isSupporterExclusive(), hasSupporter, armor.getCost())));
            if (selected && available) {
                builder.glow();
            }
            menu.setItem(2 + i, 1, builder.get(), (m, event) -> {
                if (armor.isSupporterExclusive() && !hasSupporter) {
                    sendSupporterRequired(player);
                } else if (selected) {
                    if (profile.selectArmor(null, hasSupporter)) {
                        save(databasePlayer);
                    }
                } else if (available) {
                    if (profile.selectArmor(armor, hasSupporter)) {
                        save(databasePlayer);
                    }
                } else {
                    openPurchase(player, databasePlayer, "Purchase Armor",
                            Component.text(armor.getDisplayName(), NamedTextColor.GOLD), armor.getCost(),
                            () -> profile.isUnlocked(armor), () -> {
                                profile.unlock(armor);
                                profile.selectArmor(armor, Permissions.isSupporter(player));
                            }, () -> openArmors(player));
                    return;
                }
                openArmors(player);
            });
        }
        menu.setItem(3, 3, Menu.MENU_BACK, (m, event) -> open(player));
        menu.setItem(4, 3, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.addBorder(Menu.GRAY_EMPTY_PANE, true);
        menu.openForPlayer(player);
    }

    private static List<Component> armorLore(List<Component> lore) {
        List<Component> withNote = new ArrayList<>();
        withNote.add(Component.text("Only applies to horse mounts", NamedTextColor.DARK_GRAY));
        withNote.addAll(lore);
        return withNote;
    }

    private static List<Component> optionLore(boolean available, boolean selected, boolean clearable, boolean supporterExclusive,
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
            if (clearable) {
                lore.add(Component.text("Click to remove", NamedTextColor.YELLOW));
            }
        } else if (available) {
            lore.add(Component.text("UNLOCKED", NamedTextColor.AQUA, TextDecoration.BOLD));
            lore.add(Component.text("Click to select", NamedTextColor.YELLOW));
        }
        return lore;
    }

    private static void openPurchase(Player player, DatabasePlayer databasePlayer, String title, Component name,
                                     @Nullable HonorificCost cost, BooleanSupplier unlocked, Runnable grant, Runnable returnTo) {
        if (cost == null) {
            returnTo.run();
            return;
        }
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
            save(databasePlayer);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1.2f);
            returnTo.run();
        }, (m, event) -> returnTo.run(), ignored -> {
        });
    }

    private static void normalize(DatabasePlayer databasePlayer, boolean hasSupporter) {
        if (databasePlayer.getMounts().applySupporterDefaults(hasSupporter)) {
            save(databasePlayer);
        }
    }

    private static void save(DatabasePlayer databasePlayer) {
        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
    }

    private static void sendSupporterRequired(Player player) {
        player.sendMessage(Component.text("This mount requires the Supporter rank.", NamedTextColor.RED));
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 0.7f);
    }

}
