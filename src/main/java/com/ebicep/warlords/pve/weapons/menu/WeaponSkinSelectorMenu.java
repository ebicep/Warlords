package com.ebicep.warlords.pve.weapons.menu;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.player.general.Weapons;
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
import java.util.List;

import static com.ebicep.warlords.menu.Menu.MENU_BACK;
import static com.ebicep.warlords.pve.weapons.menu.WeaponManagerMenu.openWeaponEditor;

public class WeaponSkinSelectorMenu {

    public static void openWeaponSkinSelectorMenu(Player player, AbstractWeapon weapon, int pageNumber) {
        Menu menu = new Menu("Bind Weapons", 9 * 6);

        menu.setItem(
                4,
                0,
                weapon.generateItemStack(),
                (m, e) -> {
                }
        );

        List<Weapons> weaponSkins = new ArrayList<>(Arrays.asList(Weapons.values()));
        List<Weapons> unlockedWeaponSkins = weapon.getUnlockedWeaponSkins();
        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
        for (int i = (pageNumber - 1) * 21; i < pageNumber * 21 && i < weaponSkins.size(); i++) {
            Weapons weaponSkin = weaponSkins.get(i);
            boolean isUnlocked = unlockedWeaponSkins.contains(weaponSkin);
            menu.setItem(
                    (i - (pageNumber - 1) * 21) % 7 + 1,
                    (i - (pageNumber - 1) * 21) / 7 + 1,
                    new ItemBuilder(weaponSkin.getItem())
                            .name(ChatColor.GREEN + weaponSkin.getName())
                            .lore(isUnlocked ? ChatColor.GREEN + "Unlocked" : ChatColor.GRAY + "Cost: " + ChatColor.AQUA + weaponSkin.getCost() + ChatColor.LIGHT_PURPLE + " Fairy Essence")
                            .get(),
                    (m, e) -> {
                        if (isUnlocked) {
                            if (weaponSkin != weapon.getSelectedWeaponSkin()) {
                                weapon.setSelectedWeaponSkin(weaponSkin);
                                DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                                openWeaponSkinSelectorMenu(player, weapon, pageNumber);
                            }
                        } else {
                            unlockWeaponSkin(player, weapon, weaponSkin, pageNumber);
                        }
                    }
            );
        }

        if (pageNumber > 1) {
            menu.setItem(
                    0,
                    5,
                    new ItemBuilder(Material.ARROW)
                            .name(ChatColor.GREEN + "Previous Page")
                            .lore(ChatColor.YELLOW + "Page " + (pageNumber - 1))
                            .get(),
                    (m, e) -> openWeaponSkinSelectorMenu(player, weapon, pageNumber - 1));
        }
        if (weaponSkins.size() > pageNumber * 21) {
            menu.setItem(
                    8,
                    5,
                    new ItemBuilder(Material.ARROW)
                            .name(ChatColor.GREEN + "Next Page")
                            .lore(ChatColor.YELLOW + "Page " + (pageNumber + 1))
                            .get(),
                    (m, e) -> openWeaponSkinSelectorMenu(player, weapon, pageNumber + 1));
        }

        DatabasePlayerPvE databasePlayerPvE = databasePlayer.getPveStats();
        menu.setItem(
                4,
                4,
                new ItemBuilder(Material.BOOKSHELF)
                        .name(ChatColor.LIGHT_PURPLE + "Total Fairy Essence: " + ChatColor.AQUA + databasePlayerPvE.getAmountOfFairyEssence())
                        .get(),
                (m, e) -> openWeaponEditor(player, weapon)
        );

        menu.setItem(4, 5, MENU_BACK, (m, e) -> openWeaponEditor(player, weapon));
        menu.openForPlayer(player);
    }

    public static void unlockWeaponSkin(Player player, AbstractWeapon weapon, Weapons weaponSkin, int page) {
        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
        DatabasePlayerPvE databasePlayerPvE = databasePlayer.getPveStats();
        if (databasePlayerPvE.getAmountOfFairyEssence() >= weaponSkin.getCost()) {
            weapon.setSelectedWeaponSkin(weaponSkin);
            weapon.getUnlockedWeaponSkins().add(weaponSkin);
            databasePlayerPvE.setAmountOfFairyEssence(databasePlayerPvE.getAmountOfFairyEssence() - weaponSkin.getCost());
            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
            openWeaponSkinSelectorMenu(player, weapon, page);
            player.spigot().sendMessage(
                    new TextComponent(ChatColor.GRAY + "You unlocked " + ChatColor.LIGHT_PURPLE + weaponSkin.getName() + ChatColor.GRAY + " for "),
                    new TextComponentBuilder(WeaponsPvE.getWeapon(weapon).getGeneralName())
                            .setHoverItem(weapon.generateItemStack())
                            .getTextComponent());
        } else {
            player.sendMessage(ChatColor.RED + "You don't have enough Fairy Essence to unlock this weapon skin.");
        }
    }

}
