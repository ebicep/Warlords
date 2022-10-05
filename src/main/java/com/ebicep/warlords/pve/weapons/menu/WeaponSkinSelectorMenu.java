package com.ebicep.warlords.pve.weapons.menu;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.menu.PlayerHotBarItemListener;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.WeaponsPvE;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.TextComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.ebicep.warlords.menu.Menu.MENU_BACK;
import static com.ebicep.warlords.pve.weapons.menu.WeaponManagerMenu.openWeaponEditor;

public class WeaponSkinSelectorMenu {

    public static void openWeaponSkinSelectorMenu(Player player, DatabasePlayer databasePlayer, AbstractWeapon weapon, int pageNumber) {
        Menu menu = new Menu("Bind Weapons", 9 * 6);

        menu.setItem(
                4,
                0,
                weapon.generateItemStack(),
                (m, e) -> {
                }
        );

        List<Weapons> weaponSkins = new ArrayList<>(Arrays.asList(Weapons.VALUES));
        List<Weapons> unlockedWeaponSkins = weapon.getUnlockedWeaponSkins();
        int weaponSkinCost = weapon.getRarity().fairyEssenceCost;
        for (int i = (pageNumber - 1) * 21; i < pageNumber * 21 && i < weaponSkins.size(); i++) {
            Weapons weaponSkin = weaponSkins.get(i);
            //cant reskin weapon to higher rarity than current weapon
            WeaponsPvE weaponsPvE = weaponSkin.weaponsPvE;
            if (weapon.getRarity().compareTo(weaponsPvE) < 0) {
                menu.setItem(
                        (i - (pageNumber - 1) * 21) % 7 + 1,
                        (i - (pageNumber - 1) * 21) / 7 + 1,
                        new ItemBuilder(weaponsPvE.glassItem)
                                .name(weaponsPvE.chatColor + "LOCKED")
                                .lore(
                                        ChatColor.GRAY + "This skin is locked to a weapon",
                                        ChatColor.GRAY + "of " + weaponsPvE.getChatColorName()
                                                .toUpperCase() + ChatColor.GRAY + " rarity of higher."
                                )
                                .get(),
                        (m, e) -> {
                        }
                );
            } else {
                boolean isUnlocked = unlockedWeaponSkins.contains(weaponSkin);
                ItemBuilder itemBuilder = new ItemBuilder(weaponSkin.getItem())
                        .name(ChatColor.GREEN + weaponSkin.getName())
                        .lore(
                                ChatColor.GRAY + "This change is cosmetic only \nand has no effect on gameplay.",
                                ChatColor.GRAY + "Obtain " + ChatColor.LIGHT_PURPLE + "Fairy Essence" + ChatColor.GRAY + " through \ndifferent rewards.",
                                "",
                                isUnlocked ?
                                        ChatColor.AQUA + "Cost: " + ChatColor.GREEN + "Unlocked" :
                                        ChatColor.AQUA + "Cost: \n" + ChatColor.GRAY + " - " + Currencies.FAIRY_ESSENCE.getCostColoredName(weaponSkinCost)
                        );
                if (weapon.getSelectedWeaponSkin() == weaponSkin) {
                    itemBuilder.addLore(
                            "",
                            ChatColor.YELLOW + "SELECTED"
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
                            } else {
                                if (databasePlayer.getPveStats().getCurrencyValue(Currencies.FAIRY_ESSENCE) < weaponSkinCost) {
                                    player.sendMessage(ChatColor.RED + "You need " + Currencies.FAIRY_ESSENCE.getCostColoredName(weaponSkinCost) + ChatColor.RED + " to unlock this skin.");
                                    return;
                                }
                                Menu.openConfirmationMenu(
                                        player,
                                        "Unlock Weapon Skin",
                                        3,
                                        Arrays.asList(
                                                ChatColor.GRAY + "Unlock" + ChatColor.LIGHT_PURPLE + " " + weaponSkin.getName() + ChatColor.GRAY + " Weapon Skin",
                                                "",
                                                ChatColor.AQUA + "Cost: ",
                                                ChatColor.GRAY + " - " + Currencies.FAIRY_ESSENCE.getCostColoredName(weaponSkinCost)
                                        ),
                                        Collections.singletonList(ChatColor.GRAY + "Go back"),
                                        (m2, e2) -> {
                                            unlockWeaponSkin(player, databasePlayer, weapon, weaponSkin, pageNumber);
                                            openWeaponSkinSelectorMenu(player, databasePlayer, weapon, pageNumber);
                                        },
                                        (m2, e2) -> openWeaponSkinSelectorMenu(player, databasePlayer, weapon, pageNumber),
                                        (m2) -> {
                                        }
                                );
                            }
                        }
                );
            }
        }

        if (pageNumber > 1) {
            menu.setItem(
                    0,
                    5,
                    new ItemBuilder(Material.ARROW)
                            .name(ChatColor.GREEN + "Previous Page")
                            .lore(ChatColor.YELLOW + "Page " + (pageNumber - 1))
                            .get(),
                    (m, e) -> openWeaponSkinSelectorMenu(player, databasePlayer, weapon, pageNumber - 1)
            );
        }
        if (weaponSkins.size() > pageNumber * 21) {
            menu.setItem(
                    8,
                    5,
                    new ItemBuilder(Material.ARROW)
                            .name(ChatColor.GREEN + "Next Page")
                            .lore(ChatColor.YELLOW + "Page " + (pageNumber + 1))
                            .get(),
                    (m, e) -> openWeaponSkinSelectorMenu(player, databasePlayer, weapon, pageNumber + 1)
            );
        }

        DatabasePlayerPvE databasePlayerPvE = databasePlayer.getPveStats();
        menu.setItem(
                4,
                4,
                new ItemBuilder(Material.BOOKSHELF)
                        .name(ChatColor.LIGHT_PURPLE + "Total Fairy Essence: " + ChatColor.AQUA + databasePlayerPvE.getCurrencyValue(Currencies.FAIRY_ESSENCE))
                        .get(),
                (m, e) -> openWeaponEditor(player, databasePlayer, weapon)
        );

        menu.setItem(4, 5, MENU_BACK, (m, e) -> openWeaponEditor(player, databasePlayer, weapon));
        menu.openForPlayer(player);
    }

    public static void unlockWeaponSkin(Player player, DatabasePlayer databasePlayer, AbstractWeapon weapon, Weapons weaponSkin, int page) {
        DatabasePlayerPvE databasePlayerPvE = databasePlayer.getPveStats();
        if (databasePlayerPvE.getWeaponInventory().contains(weapon)) {
            weapon.setSelectedWeaponSkin(weaponSkin);
            weapon.getUnlockedWeaponSkins().add(weaponSkin);
            databasePlayerPvE.subtractCurrency(Currencies.FAIRY_ESSENCE, weapon.getRarity().fairyEssenceCost);
            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);

            player.spigot().sendMessage(
                    new TextComponent(ChatColor.GRAY + "You unlocked " + ChatColor.LIGHT_PURPLE + weaponSkin.getName() + ChatColor.GRAY + " for "),
                    new TextComponentBuilder(weapon.getName())
                            .setHoverItem(weapon.generateItemStack())
                            .getTextComponent()
            );
            PlayerHotBarItemListener.updateWeaponManagerItem(player, databasePlayer);

            openWeaponSkinSelectorMenu(player, databasePlayer, weapon, page);
        }
    }

}
