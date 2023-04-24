package com.ebicep.warlords.pve.weapons.menu;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryWeaponTitleInfo;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import de.rapha149.signgui.SignGUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static com.ebicep.warlords.menu.Menu.MENU_BACK;
import static com.ebicep.warlords.pve.weapons.menu.WeaponManagerMenu.openWeaponEditor;

public class WeaponTitleMenu {

    public static void openWeaponTitleMenu(Player player, DatabasePlayer databasePlayer, AbstractLegendaryWeapon weapon, LegendaryTitles[] titles, int page) {
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

        Map<LegendaryTitles, LegendaryWeaponTitleInfo> unlockedTitles = weapon.getTitles();
        for (int i = 0; i < 3; i++) {
            int titleIndex = ((page - 1) * 3) + i;
            if (titleIndex < titles.length) {
                LegendaryTitles title = titles[titleIndex];
                AbstractLegendaryWeapon titledWeapon = title.titleWeapon.apply(weapon);
                ItemBuilder itemBuilder = new ItemBuilder(titledWeapon.generateItemStack(false));

                Set<Map.Entry<Currencies, Long>> cost = titledWeapon.getCost().entrySet();
                List<Component> loreCost = titledWeapon.getCostLore();

                boolean equals = Objects.equals(weapon.getTitle(), title);
                boolean titleIsLocked = !unlockedTitles.containsKey(title);
                if (equals) {
                    itemBuilder.addLore(
                            Component.empty(),
                            Component.text("Selected", NamedTextColor.GREEN)
                    );
                    itemBuilder.enchant(Enchantment.OXYGEN, 1);
                    itemBuilder.flags(ItemFlag.HIDE_ENCHANTS);
                } else {
                    if (titleIsLocked) {
                        itemBuilder.addLoreC(loreCost);
                    } else {
                        itemBuilder.addLore(
                                Component.empty(),
                                Component.text("Click to Select", NamedTextColor.GREEN)
                        );
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
                            List<Component> confirmLore = new ArrayList<>();
                            String titleName = titledWeapon.getTitleName();
                            if (titleName.isEmpty()) {
                                confirmLore.add(Component.text("Remove ", NamedTextColor.GRAY)
                                                         .append(Component.text(weapon.getTitleName(), NamedTextColor.GREEN))
                                                         .append(Component.text(" title"))
                                );
                            } else {
                                confirmLore.add(Component.text("Apply ", NamedTextColor.GRAY)
                                                         .append(Component.text(titleName, NamedTextColor.GREEN))
                                                         .append(Component.text(" title"))
                                );
                            }
                            if (titleIsLocked) {
                                confirmLore.addAll(loreCost);
                            }
                            Menu.openConfirmationMenu0(
                                    player,
                                    "Apply Title",
                                    3,
                                    confirmLore,
                                    Menu.GO_BACK,
                                    (m2, e2) -> {
                                        AbstractLegendaryWeapon newTitledWeapon = titleWeapon(player, databasePlayer, weapon, title);
                                        openWeaponTitleMenu(player, databasePlayer, newTitledWeapon, titles, page);

                                    },
                                    (m2, e2) -> openWeaponTitleMenu(player, databasePlayer, weapon, titles, page),
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
                            .loreLEGACY(ChatColor.YELLOW + "Page " + (page - 1))
                            .get(),
                    (m, e) -> openWeaponTitleMenu(player, databasePlayer, weapon, titles, page - 1)
            );
        }
        if (titles.length > (page * 3)) {
            menu.setItem(8, 4,
                    new ItemBuilder(Material.ARROW)
                            .name(ChatColor.GREEN + "Next Page")
                            .loreLEGACY(ChatColor.YELLOW + "Page " + (page + 1))
                            .get(),
                    (m, e) -> openWeaponTitleMenu(player, databasePlayer, weapon, titles, page + 1)
            );
        }

        menu.setItem(4, 4, MENU_BACK, (m, e) -> openWeaponEditor(player, databasePlayer, weapon));
        menu.setItem(5, 4,
                new ItemBuilder(Material.OAK_SIGN)
                        .name(ChatColor.GREEN + "Search Title")
                        .get(),
                (m, e) ->
                        new SignGUI()
                                .lines("", "^ Search Query ^", "Returns titles", "containing query")
                                .onFinish((p, lines) -> {
                                    String titleName = lines[0];
                                    if (titleName.isEmpty()) {
                                        player.sendMessage(ChatColor.RED + "Query cannot be empty!");
                                        openWeaponEditorAfterTick(player, databasePlayer, weapon);
                                        return null;
                                    }
                                    titleName = titleName.toLowerCase();
                                    String finalTitleName = titleName;
                                    LegendaryTitles[] legendaryTitles = Arrays.stream(LegendaryTitles.VALUES)
                                                                              .filter(title -> title.name.toLowerCase().contains(finalTitleName))
                                                                              .toArray(LegendaryTitles[]::new);
                                    if (legendaryTitles.length == 0) {
                                        player.sendMessage(ChatColor.RED + "No titles with that name found!");
                                        openWeaponEditorAfterTick(player, databasePlayer, weapon);
                                    } else {
                                        new BukkitRunnable() {
                                            @Override
                                            public void run() {
                                                openWeaponTitleMenu(player, databasePlayer, weapon, legendaryTitles, 1);
                                            }
                                        }.runTaskLater(Warlords.getInstance(), 1);
                                    }
                                    return null;
                                }).open(player)
        );
        menu.openForPlayer(player);
    }

    private static void openWeaponEditorAfterTick(Player player, DatabasePlayer databasePlayer, AbstractLegendaryWeapon weapon) {
        new BukkitRunnable() {
            @Override
            public void run() {
                openWeaponEditor(player, databasePlayer, weapon);
            }
        }.runTaskLater(Warlords.getInstance(), 1);
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

    public static void openWeaponTitleMenu(Player player, DatabasePlayer databasePlayer, AbstractLegendaryWeapon weapon, int page) {
        openWeaponTitleMenu(player, databasePlayer, weapon, LegendaryTitles.VALUES, page);
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
                        .name(Menu.DENY)
                        .lore(WeaponManagerMenu.GO_BACK)
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
            LinkedHashMap<Spendable, Long> upgradeCost = weapon.getTitleUpgradeCost(weapon.getTitleLevelUpgraded());
            for (Map.Entry<Spendable, Long> currenciesLongEntry : upgradeCost.entrySet()) {
                currenciesLongEntry.getKey().subtractFromPlayer(databasePlayer, currenciesLongEntry.getValue());
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
