package com.ebicep.warlords.pve.weapons.menu;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.weaponaddons.StatsRerollable;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.TextComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class WeaponRerollMenu {

    public static void openWeaponRerollMenu(Player player, AbstractWeapon weapon) {
        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());

        if (databasePlayer == null) return;

        Menu menu = new Menu("Confirm Reroll", 9 * 3);

        StatsRerollable weaponRerollable = (StatsRerollable) weapon;
        int rerollCost = weaponRerollable.getRerollCost();
        menu.setItem(2, 1,
                new ItemBuilder(Material.STAINED_CLAY, 1, (short) 13)
                        .name(ChatColor.GREEN + "Confirm")
                        .lore(
                                ChatColor.GRAY + "Reroll this weapon and reset its stats.",
                                "",
                                ChatColor.GRAY + "Cost: " + ChatColor.GOLD + rerollCost + " coins",
                                "",
                                ChatColor.RED + "WARNING: " + ChatColor.GRAY + "This action cannot be undone."
                        )
                        .get(),
                (m, e) -> {
                    if (databasePlayer.getPveStats().getCoins() >= rerollCost) {
                        databasePlayer.getPveStats().addCoins(-rerollCost);
                        rerollWeapon(player, weapon);
                    } else {
                        player.sendMessage(ChatColor.RED + "You do not have enough coins to reroll this weapon.");
                    }
                    WeaponManagerMenu.openWeaponEditor(player, weapon);
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

    public static void rerollWeapon(Player player, AbstractWeapon weapon) {
        if (!(weapon instanceof StatsRerollable)) {
            return;
        }
        StatsRerollable rerollable = (StatsRerollable) weapon;

        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
        if (databasePlayer.getPveStats().getWeaponInventory().contains(weapon)) {
            rerollable.reroll();

            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);

            player.spigot().sendMessage(
                    new TextComponent(ChatColor.GRAY + "Reroll Result: "),
                    new TextComponentBuilder(weapon.getName())
                            .setHoverItem(weapon.generateItemStack())
                            .getTextComponent());
        }
    }

}
