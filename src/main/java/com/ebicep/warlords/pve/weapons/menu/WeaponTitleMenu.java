package com.ebicep.warlords.pve.weapons.menu;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryWeaponTitleInfo;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.*;

import static com.ebicep.warlords.menu.Menu.MENU_BACK;
import static com.ebicep.warlords.pve.weapons.menu.WeaponManagerMenu.openWeaponEditor;

public class WeaponTitleMenu {

    public static void openWeaponTitleMenu(Player player, DatabasePlayer databasePlayer, AbstractLegendaryWeapon weapon, int page) {
        Menu menu = new Menu("Apply Title to Weapon", 9 * 5);

        for (int i = 0; i < 9 * 5; i++) {
            menu.addItem(
                    new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                            .name(" ")
                            .get(),
                    (m, e) -> {
                    }
            );
        }

        menu.setItem(
                4,
                0,
                weapon.generateItemStack(false),
                (m, e) -> {
                }
        );

//        int[] colors = new int[] {4,5,3};
//        for (int i = 0; i < 9; i++) {
//            for (int j = 0; j < 3; j++) {
//                menu.setItem(
//                        i,
//                        j + 1,
//                        new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) colors[i / 3])
//                                .name("")
//                                .get(),
//                        (m, e) -> {
//                        }
//                );
//            }
//        }

        Map<LegendaryTitles, LegendaryWeaponTitleInfo> unlockedTitles = weapon.getTitles();
        for (int i = 0; i < 3; i++) {
            int titleIndex = ((page - 1) * 3) + i;
            if (titleIndex < LegendaryTitles.VALUES.length) {
                LegendaryTitles title = LegendaryTitles.VALUES[titleIndex];
                AbstractLegendaryWeapon titledWeapon = title.titleWeapon.apply(weapon);
                ItemBuilder itemBuilder = new ItemBuilder(titledWeapon.generateItemStack(false));

                Set<Map.Entry<Currencies, Long>> cost = titledWeapon.getCost().entrySet();
                List<String> loreCost = titledWeapon.getCostLore();

                boolean equals = Objects.equals(weapon.getTitle(), title);
                boolean titleIsLocked = !unlockedTitles.containsKey(title);
                if (equals) {
                    itemBuilder.addLore("", ChatColor.GREEN + "Selected");
                    itemBuilder.enchant(Enchantment.OXYGEN, 1);
                    itemBuilder.flags(ItemFlag.HIDE_ENCHANTS);
                } else {
                    if (titleIsLocked) {
                        itemBuilder.addLore(loreCost);
                    } else {
                        itemBuilder.addLore("", ChatColor.GREEN + "Click to Select");
                    }
                }
                for (int k = 0; k < 3; k++) {
                    for (int j = 0; j < 3; j++) {
                        if (j == 1) {
                            menu.setItem(
                                    k + i * 3,
                                    j + 1,
                                    null,
                                    (m, e) -> {
                                    }
                            );
                            continue;
                        }
                        menu.setItem(
                                k + i * 3,
                                j + 1,
                                new ItemBuilder(title.glassPane)
                                        .name(" ")
                                        .get(),
                                (m, e) -> {
                                }
                        );
                    }
                }
                menu.setItem((i % 3) * 3 + 1, 2,
                        itemBuilder.get(),
                        (m, e) -> {
                            if (equals) {
                                player.sendMessage(ChatColor.RED + "You already have this title on your weapon!");
                                return;
                            }
                            if (titleIsLocked) {
                                DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
                                for (Map.Entry<Currencies, Long> currenciesLongEntry : cost) {
                                    Currencies currency = currenciesLongEntry.getKey();
                                    Long currencyCost = currenciesLongEntry.getValue();
                                    if (pveStats.getCurrencyValue(currency) < currencyCost) {
                                        player.sendMessage(ChatColor.RED + "You need " + currency.getCostColoredName(currencyCost) + ChatColor.RED + " to apply this title!");
                                        return;
                                    }
                                }
                            }
                            List<String> confirmLore = new ArrayList<>();
                            String titleName = titledWeapon.getTitleName();
                            if (titleName.isEmpty()) {
                                confirmLore.add(ChatColor.GRAY + "Remove " + ChatColor.GREEN + weapon.getTitleName() + ChatColor.GRAY + " title");
                            } else {
                                confirmLore.add(ChatColor.GRAY + "Apply " + ChatColor.GREEN + titleName + ChatColor.GRAY + " title");
                            }
                            if (titleIsLocked) {
                                confirmLore.addAll(loreCost);
                            }
                            Menu.openConfirmationMenu(
                                    player,
                                    "Apply Title",
                                    3,
                                    confirmLore,
                                    Collections.singletonList(ChatColor.GRAY + "Go back"),
                                    (m2, e2) -> {
                                        AbstractLegendaryWeapon newTitledWeapon = titleWeapon(player, databasePlayer, weapon, title);
                                        openWeaponTitleMenu(player, databasePlayer, newTitledWeapon, page);

                                    },
                                    (m2, e2) -> openWeaponTitleMenu(player, databasePlayer, weapon, page),
                                    (m2) -> {
                                    }
                            );
                        }
                );
            }
        }

        if (page - 1 > 0) {
            menu.setItem(0, 4,
                    new ItemBuilder(Material.ARROW)
                            .name(ChatColor.GREEN + "Previous Page")
                            .lore(ChatColor.YELLOW + "Page " + (page - 1))
                            .get(),
                    (m, e) -> openWeaponTitleMenu(player, databasePlayer, weapon, page - 1)
            );
        }
        if (LegendaryTitles.VALUES.length > (page * 3)) {
            menu.setItem(8, 4,
                    new ItemBuilder(Material.ARROW)
                            .name(ChatColor.GREEN + "Next Page")
                            .lore(ChatColor.YELLOW + "Page " + (page + 1))
                            .get(),
                    (m, e) -> openWeaponTitleMenu(player, databasePlayer, weapon, page + 1)
            );
        }

        menu.setItem(4, 4, MENU_BACK, (m, e) -> openWeaponEditor(player, databasePlayer, weapon));
        menu.openForPlayer(player);
    }

    public static AbstractLegendaryWeapon titleWeapon(Player player, DatabasePlayer databasePlayer, AbstractLegendaryWeapon weapon, LegendaryTitles title) {
        List<AbstractWeapon> weaponInventory = databasePlayer.getPveStats().getWeaponInventory();
        if (!weapon.getTitles().containsKey(title)) {
            DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
            weapon.getCost().forEach(pveStats::subtractCurrency);
            weapon.getTitles().put(title, new LegendaryWeaponTitleInfo());
        }
        AbstractLegendaryWeapon titledWeapon = title.titleWeapon.apply(weapon);
        weaponInventory.remove(weapon);
        weaponInventory.add(titledWeapon);
        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);

        player.sendMessage(Component.text(ChatColor.GRAY + "Titled Weapon: ")
                                    .append(weapon.getHoverComponent(false))
                                    .append(Component.text(ChatColor.GRAY + " and it became "))
                                    .append(titledWeapon.getHoverComponent(false))
                                    .append(Component.text(ChatColor.GRAY + "!"))
        );

        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 500, 2);

        return titledWeapon;
    }

    public static void openWeaponTitleUpgradeMenu(Player player, DatabasePlayer databasePlayer, AbstractLegendaryWeapon weapon) {
        if (weapon == null) {
            return;
        }

        Menu menu = new Menu("Upgrade Weapon Title", 9 * 3);

        menu.setItem(2, 1,
                weapon.getUpgradedTitleItem(),
                (m, e) -> {
                    upgradeWeaponTitle(player, databasePlayer, weapon);
                    WeaponManagerMenu.openWeaponEditor(player, databasePlayer, weapon);
                }
        );

        menu.setItem(4, 1,
                weapon.generateItemStack(false),
                (m, e) -> {
                }
        );

        menu.setItem(6, 1,
                new ItemBuilder(Material.RED_CONCRETE)
                        .name(ChatColor.RED + "Deny")
                        .lore(ChatColor.GRAY + "Go back.")
                        .get(),
                (m, e) -> WeaponManagerMenu.openWeaponEditor(player, databasePlayer, weapon)
        );

        menu.openForPlayer(player);

    }

    public static void upgradeWeaponTitle(Player player, DatabasePlayer databasePlayer, AbstractLegendaryWeapon weapon) {
        if (weapon == null) {
            return;
        }
        if (databasePlayer.getPveStats().getWeaponInventory().contains(weapon)) {
            LinkedHashMap<Currencies, Long> upgradeCost = weapon.getTitleUpgradeCost(weapon.getTitleLevelUpgraded());
            for (Map.Entry<Currencies, Long> currenciesLongEntry : upgradeCost.entrySet()) {
                databasePlayer.getPveStats().subtractCurrency(currenciesLongEntry.getKey(), currenciesLongEntry.getValue());
            }
            weapon.upgradeTitleLevel();
            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);

            player.sendMessage(Component.text(ChatColor.GRAY + "Upgraded Weapon Title: ")
                                        .append(weapon.getHoverComponent(false))
            );
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 500, 2);
        }
    }


}
