package com.ebicep.warlords.pve.weapons.menu;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.pve.PvEUtils;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.WeaponSkinCost;
import com.ebicep.warlords.pve.weapons.WeaponsPvE;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.ebicep.warlords.menu.Menu.MENU_BACK;
import static com.ebicep.warlords.pve.weapons.menu.WeaponManagerMenu.openWeaponEditor;

public class WeaponSkinSelectorMenu {

    public static void openWeaponSkinSelectorMenu(Player player, DatabasePlayer databasePlayer, AbstractWeapon weapon, int pageNumber) {
        Menu menu = new Menu("Weapon Skins", 9 * 6);

        menu.setItem(
                4,
                0,
                weapon.generateItemStack(false),
                (m, e) -> {
                }
        );

        List<Weapons> weaponSkins = new ArrayList<>(Arrays.asList(Weapons.VALUES));
        List<Weapons> unlockedWeaponSkins = weapon.getUnlockedWeaponSkins();

        for (int i = (pageNumber - 1) * 21; i < pageNumber * 21 && i < weaponSkins.size(); i++) {
            Weapons weaponSkin = weaponSkins.get(i);
            WeaponsPvE weaponsPvE = weaponSkin.weaponsPvE;
            if (weapon.getRarity().compareTo(weaponsPvE) < 0) {
                menu.setItem(
                        (i - (pageNumber - 1) * 21) % 7 + 1,
                        (i - (pageNumber - 1) * 21) / 7 + 1,
                        new ItemBuilder(weaponsPvE.glassItem)
                                .name(Component.text("LOCKED", weaponsPvE.textColor))
                                .lore(Component.text("This skin is locked to a weapon", NamedTextColor.GRAY),
                                        Component.text("of ", NamedTextColor.GRAY)
                                                 .append(Component.text(weaponsPvE.name.toUpperCase(), weaponsPvE.textColor))
                                                 .append(Component.text(" rarity of higher."))
                                )
                                .get(),
                        (m, e) -> {
                        }
                );
            } else {
                boolean isUnlocked = unlockedWeaponSkins.contains(weaponSkin);
                WeaponSkinCost skinCost = WeaponSkinCost.get(weaponSkin, weapon.getRarity());
                Spendable currency = skinCost.currency();
                long amount = skinCost.amount();
                LinkedHashMap<Spendable, Long> cost = new LinkedHashMap<>();
                cost.put(currency, amount);

                ItemBuilder itemBuilder = new ItemBuilder(weaponSkin.getItem())
                        .name(Component.text(weaponSkin.getName(), NamedTextColor.GREEN))
                        .lore(WordWrap.wrap(Component.text("This change is cosmetic only and has no effect on gameplay.", NamedTextColor.GRAY), 150))
                        .addLore(
                                Component.text("Currency: ", NamedTextColor.GRAY)
                                         .append(Component.text(currency.getName(), currency.getTextColor())),
                                Component.empty()
                        );
                if (isUnlocked) {
                    itemBuilder.addLore(
                            Component.text("Cost: ", NamedTextColor.AQUA).append(Component.text("Unlocked", NamedTextColor.GREEN))
                    );
                } else {
                    itemBuilder.addLore(PvEUtils.getCostLore(cost, false));
                }
                if (weapon.getSelectedWeaponSkin() == weaponSkin) {
                    itemBuilder.addLore(
                            Component.empty(),
                            Component.text("SELECTED", NamedTextColor.YELLOW)
                    );
                }
                menu.setItem(
                        (i - (pageNumber - 1) * 21) % 7 + 1,
                        (i - (pageNumber - 1) * 21) / 7 + 1,
                        itemBuilder.get(),
                        (m, e) -> {
                            if (isUnlocked) {
                                if (weaponSkin != weapon.getSelectedWeaponSkin()) {
                                    weapon.setSelectedWeaponSkin(weaponSkin);
                                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                                    openWeaponSkinSelectorMenu(player, databasePlayer, weapon, pageNumber);
                                }
                                return;
                            }
                            if (currency.getFromPlayer(databasePlayer) < amount) {
                                player.sendMessage(Component.text("You need ", NamedTextColor.RED)
                                                            .append(currency.getCostColoredName(amount))
                                                            .append(Component.text(" to unlock this skin."))
                                );
                                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 2, 0.5f);
                                return;
                            }
                            Menu.openConfirmationMenu(
                                    player,
                                    "Unlock Weapon Skin",
                                    3,
                                    new ArrayList<>() {{
                                        add(Component.text("Unlock ", NamedTextColor.GRAY)
                                                     .append(Component.text(weaponSkin.getName(), NamedTextColor.LIGHT_PURPLE))
                                                     .append(Component.text(" Weapon Skin")));
                                        add(Component.empty());
                                        addAll(PvEUtils.getCostLore(cost, false));
                                    }},
                                    Collections.singletonList(Component.text("Go back", NamedTextColor.GRAY)),
                                    (m2, e2) -> {
                                        if (unlockWeaponSkin(player, databasePlayer, weapon, weaponSkin)) {
                                            openWeaponSkinSelectorMenu(player, databasePlayer, weapon, pageNumber);
                                        }
                                    },
                                    (m2, e2) -> openWeaponSkinSelectorMenu(player, databasePlayer, weapon, pageNumber),
                                    (m2) -> {
                                    }
                            );
                        }
                );
            }
        }

        if (pageNumber > 1) {
            menu.setItem(
                    0,
                    5,
                    new ItemBuilder(Material.ARROW)
                            .name(Component.text("Previous Page", NamedTextColor.GREEN))
                            .lore(Component.text("Page " + (pageNumber - 1), NamedTextColor.YELLOW))
                            .get(),
                    (m, e) -> openWeaponSkinSelectorMenu(player, databasePlayer, weapon, pageNumber - 1)
            );
        }
        if (weaponSkins.size() > pageNumber * 21) {
            menu.setItem(
                    8,
                    5,
                    new ItemBuilder(Material.ARROW)
                            .name(Component.text("Next Page", NamedTextColor.GREEN))
                            .lore(Component.text("Page " + (pageNumber + 1), NamedTextColor.YELLOW))
                            .get(),
                    (m, e) -> openWeaponSkinSelectorMenu(player, databasePlayer, weapon, pageNumber + 1)
            );
        }

        Set<Spendable> skinCurrencies = new LinkedHashSet<>();
        for (Weapons weaponSkin : Weapons.VALUES) {
            skinCurrencies.add(WeaponSkinCost.getCurrency(weaponSkin));
        }
        List<Component> currencyBalances = skinCurrencies.stream()
                                                          .map(currency -> currency.getCostColoredName(currency.getFromPlayer(databasePlayer)))
                                                          .toList();
        menu.setItem(
                4,
                4,
                new ItemBuilder(Material.BOOKSHELF)
                        .name(Component.text("Skin Currency Balances", NamedTextColor.LIGHT_PURPLE))
                        .lore(currencyBalances)
                        .get(),
                (m, e) -> openWeaponEditor(player, databasePlayer, weapon)
        );

        menu.setItem(4, 5, MENU_BACK, (m, e) -> openWeaponEditor(player, databasePlayer, weapon));
        menu.openForPlayer(player);
    }

    public static boolean unlockWeaponSkin(Player player, DatabasePlayer databasePlayer, AbstractWeapon weapon, Weapons weaponSkin) {
        DatabasePlayerPvE databasePlayerPvE = databasePlayer.getPveStats();
        if (!databasePlayerPvE.getWeaponInventory().contains(weapon)) {
            return false;
        }
        if (weapon.getRarity().compareTo(weaponSkin.weaponsPvE) < 0) {
            return false;
        }

        if (weapon.getUnlockedWeaponSkins().contains(weaponSkin)) {
            weapon.setSelectedWeaponSkin(weaponSkin);
            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
            return true;
        }

        WeaponSkinCost skinCost = WeaponSkinCost.get(weaponSkin, weapon.getRarity());
        Spendable currency = skinCost.currency();
        long amount = skinCost.amount();
        if (currency.getFromPlayer(databasePlayer) < amount) {
            player.sendMessage(Component.text("You need ", NamedTextColor.RED)
                                        .append(currency.getCostColoredName(amount))
                                        .append(Component.text(" to unlock this skin."))
            );
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 2, 0.5f);
            return false;
        }

        currency.subtractFromPlayer(databasePlayer, amount);
        weapon.setSelectedWeaponSkin(weaponSkin);
        weapon.getUnlockedWeaponSkins().add(weaponSkin);
        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);

        player.sendMessage(Component.text("You unlocked ", NamedTextColor.GRAY)
                                    .append(Component.text(weaponSkin.getName(), NamedTextColor.LIGHT_PURPLE))
                                    .append(Component.text(" for ")
                                                     .append(weapon.getHoverComponent(false))
                                    )
        );
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 500, 2);
        return true;
    }
}
