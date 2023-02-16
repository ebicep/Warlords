package com.ebicep.warlords.pve.weapons.menu;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.weaponaddons.Salvageable;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class WeaponSalvageMenu {

    public static <T extends AbstractWeapon & Salvageable> void openWeaponSalvageConfirmMenu(Player player, DatabasePlayer databasePlayer, T weapon) {
        Menu menu = new Menu("Confirm salvage", 9 * 3);

        menu.setItem(2, 1,
                new ItemBuilder(Material.GREEN_CONCRETE)
                        .name(ChatColor.GREEN + "Confirm")
                        .lore(
                                ChatColor.GRAY + "Salvage this weapon and claim its materials.",
                                "",
                                ChatColor.RED + "WARNING: " + ChatColor.GRAY + "This action cannot be undone."
                        )
                        .get(),
                (m, e) -> {
                    salvageWeapon(player, databasePlayer, weapon);
                    WeaponManagerMenu.openWeaponInventoryFromInternal(player, databasePlayer);
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

    public static <T extends AbstractWeapon & Salvageable> void salvageWeapon(Player player, DatabasePlayer databasePlayer, T weapon) {
        if (weapon == null) {
            return;
        }
        int salvageAmount = weapon.getSalvageAmount();
        if (databasePlayer.getPveStats().getWeaponInventory().contains(weapon)) {
            databasePlayer.getPveStats().getWeaponInventory().remove(weapon);
            databasePlayer.getPveStats().addCurrency(Currencies.SYNTHETIC_SHARD, salvageAmount);
            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);

            player.sendMessage(Component.text(ChatColor.GRAY + "You received " + ChatColor.WHITE + salvageAmount + " Synthetic Shard" + (salvageAmount == 1 ? "" : "s") + ChatColor.GRAY + " from salvaging ")
                                        .append(weapon.getHoverComponent(false)));

            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2, 2);
            player.playSound(player.getLocation(), "rogue.remedicchains.impact", 0.1f, 1);
        }
    }
}
