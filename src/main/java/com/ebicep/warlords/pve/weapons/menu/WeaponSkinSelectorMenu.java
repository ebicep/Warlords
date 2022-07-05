package com.ebicep.warlords.pve.weapons.menu;

import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
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
        for (int i = (pageNumber - 1) * 21; i < pageNumber * 21 && i < weaponSkins.size(); i++) {
            Weapons weaponSkin = weaponSkins.get(i);
            menu.setItem(
                    (i - (pageNumber - 1) * 21) % 7 + 1,
                    (i - (pageNumber - 1) * 21) / 7 + 1,
                    new ItemBuilder(weaponSkin.getItem())
                            .name(ChatColor.GREEN + weaponSkin.getName())
                            .get(),
                    (m, e) -> {

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


        menu.setItem(4, 5, MENU_BACK, (m, e) -> openWeaponEditor(player, weapon));
        menu.openForPlayer(player);
    }

}
