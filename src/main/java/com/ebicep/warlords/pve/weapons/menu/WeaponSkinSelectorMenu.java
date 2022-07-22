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
            //cant reskin weapon to higher rarity than current weapon
            WeaponsPvE weaponsPvE = weaponSkin.weaponsPvE;
            if (WeaponsPvE.getWeapon(weapon).compareTo(weaponsPvE) < 0) {
                menu.setItem(
                        (i - (pageNumber - 1) * 21) % 7 + 1,
                        (i - (pageNumber - 1) * 21) / 7 + 1,
                        new ItemBuilder(weaponsPvE.glassItem)
                                .name(weaponsPvE.chatColor + "LOCKED")
                                .lore(
                                        ChatColor.GRAY + "This skin is locked to a weapon",
                                        ChatColor.GRAY + "of " + weaponsPvE.getChatColorName().toUpperCase() + ChatColor.GRAY + " rarity of higher."
                                )
                                .get(),
                        (m, e) -> {
                        }
                );
            } else {
                boolean isUnlocked = unlockedWeaponSkins.contains(weaponSkin);
                menu.setItem(
                        (i - (pageNumber - 1) * 21) % 7 + 1,
                        (i - (pageNumber - 1) * 21) / 7 + 1,
                        new ItemBuilder(weaponSkin.getItem())
                                .name(ChatColor.GREEN + weaponSkin.getName())
                                .lore(
                                        ChatColor.GRAY + "This change is cosmetic only \nand has no effect on gameplay.",
                                        ChatColor.GRAY + "Obtain " + ChatColor.LIGHT_PURPLE + "Fairy Essence" + ChatColor.GRAY + " through \ndifferent rewards.",
                                        "",
                                        isUnlocked ? ChatColor.GRAY + "Cost: " + ChatColor.GREEN + "UNLOCKED" : ChatColor.GRAY + "Cost: " + ChatColor.LIGHT_PURPLE + weaponSkin.getCost() + " Fairy Essence"
                                )
                                .get(),
                        (m, e) -> {
                            if (isUnlocked) {
                                if (weaponSkin != weapon.getSelectedWeaponSkin()) {
                                    weapon.setSelectedWeaponSkin(weaponSkin);
                                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                                }
                            } else {
                                if (databasePlayer.getPveStats().getFairyEssence() < weaponSkin.getCost()) {
                                    player.sendMessage(ChatColor.RED + "You do not have enough Fairy Essence to purchase this skin.");
                                    return;
                                }
                                unlockWeaponSkin(player, weapon, weaponSkin, pageNumber);
                            }
                            openWeaponSkinSelectorMenu(player, weapon, pageNumber);
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
                        .name(ChatColor.LIGHT_PURPLE + "Total Fairy Essence: " + ChatColor.AQUA + databasePlayerPvE.getFairyEssence())
                        .get(),
                (m, e) -> openWeaponEditor(player, weapon)
        );

        menu.setItem(4, 5, MENU_BACK, (m, e) -> openWeaponEditor(player, weapon));
        menu.openForPlayer(player);
    }

    public static void unlockWeaponSkin(Player player, AbstractWeapon weapon, Weapons weaponSkin, int page) {
        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
        DatabasePlayerPvE databasePlayerPvE = databasePlayer.getPveStats();
        if (databasePlayerPvE.getWeaponInventory().contains(weapon)) {
            weapon.setSelectedWeaponSkin(weaponSkin);
            weapon.getUnlockedWeaponSkins().add(weaponSkin);
            databasePlayerPvE.addFairyEssence(-weaponSkin.getCost());
            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
            openWeaponSkinSelectorMenu(player, weapon, page);
            player.spigot().sendMessage(
                    new TextComponent(ChatColor.GRAY + "You unlocked " + ChatColor.LIGHT_PURPLE + weaponSkin.getName() + ChatColor.GRAY + " for "),
                    new TextComponentBuilder(weapon.getName())
                            .setHoverItem(weapon.generateItemStack())
                            .getTextComponent());
        }
    }

}
