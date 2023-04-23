package com.ebicep.warlords.pve.weapons.menu;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.weaponaddons.StatsRerollable;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class WeaponRerollMenu {

    public static <T extends AbstractWeapon & StatsRerollable> void openWeaponRerollMenu(Player player, DatabasePlayer databasePlayer, T weapon) {
        Menu menu = new Menu("Confirm Reroll", 9 * 3);

        int rerollCost = weapon.getRerollCost();
        menu.setItem(2, 1,
                new ItemBuilder(Material.GREEN_CONCRETE)
                        .name(ChatColor.GREEN + "Confirm")
                        .loreLEGACY(ChatColor.GRAY + "Reroll this weapon and reset its stats.")
                        .addLore(weapon.getRerollCostLore())
                        .addLore(
                                "",
                                ChatColor.RED + "WARNING: " + ChatColor.GRAY + "This action cannot be undone."
                        )
                        .get(),
                (m, e) -> {
                    databasePlayer.getPveStats().subtractCurrency(Currencies.COIN, rerollCost);
                    rerollWeapon(player, databasePlayer, weapon);
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
                        .loreLEGACY(ChatColor.GRAY + "Go back.")
                        .get(),
                (m, e) -> WeaponManagerMenu.openWeaponEditor(player, databasePlayer, weapon)
        );

        menu.openForPlayer(player);
    }

    public static <T extends AbstractWeapon & StatsRerollable> void rerollWeapon(Player player, DatabasePlayer databasePlayer, T weapon) {
        if (weapon == null) {
            return;
        }
        if (databasePlayer.getPveStats().getWeaponInventory().contains(weapon)) {
            Component component = Component.text(ChatColor.GRAY + "Reroll Result: ")
                                           .append(weapon.getHoverComponent(false));
            weapon.reroll();
            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);

            player.sendMessage(component.append(Component.text(ChatColor.GRAY + " to "))
                                        .append(weapon.getHoverComponent(false))
            );
        }
    }

}
