package com.ebicep.warlords.pve.weapons.menu;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.Upgradeable;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class WeaponUpgradeMenu {

    public static void openWeaponUpgradeMenu(Player player, AbstractWeapon weapon) {
        if (!(weapon instanceof Upgradeable)) {
            return;
        }
        Upgradeable upgradeable = (Upgradeable) weapon;

        Menu menu = new Menu("Upgrade Weapon", 9 * 3);

        menu.setItem(2, 1,
                upgradeable.getUpgradeItem(),
                (m, e) -> {
                    if (true) {
                        if (upgradeable.getUpgradeLevel() == upgradeable.getMaxUpgradeLevel()) {
                            player.sendMessage(ChatColor.RED + "You can't upgrade this weapon anymore.");
                            return;
                        }
                        upgradeWeapon(player, weapon);
                        WeaponManagerMenu.openWeaponEditor(player, weapon);
                    } else {
                        player.sendMessage(ChatColor.RED + "You don't have enough PLACEHOLDER!");
                    }
                }
        );

        menu.setItem(4, 1,
                weapon.generateItemStack(),
                (m, e) -> {
                }
        );

        menu.setItem(6, 1,
                new ItemBuilder(Material.STAINED_CLAY, 1, (short) 14)
                        .name(ChatColor.RED + "Deny")
                        .lore(ChatColor.GRAY + "Go back.")
                        .get(),
                (m, e) -> WeaponManagerMenu.openWeaponEditor(player, weapon)
        );

        menu.openForPlayer(player);
    }

    public static void upgradeWeapon(Player player, AbstractWeapon weapon) {
        if (!(weapon instanceof Upgradeable)) {
            return;
        }
        Upgradeable upgradeable = (Upgradeable) weapon;

        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
        if (databasePlayer.getPveStats().getWeaponInventory().contains(weapon)) {
            upgradeable.upgrade();
            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
        }

    }

}
