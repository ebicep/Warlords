package com.ebicep.warlords.pve.weapons.menu;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.weaponaddons.StatsRerollable;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.TextComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class WeaponRerollMenu {

    public static void openWeaponRerollMenu(Player player, DatabasePlayer databasePlayer, AbstractWeapon weapon) {
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
                    if (databasePlayer.getPveStats().getCurrencyValue(Currencies.COIN) >= rerollCost) {
                        databasePlayer.getPveStats().subtractCurrency(Currencies.COIN, rerollCost);
                        rerollWeapon(player, databasePlayer, (AbstractWeapon & StatsRerollable) weapon);
                    } else {
                        player.sendMessage(ChatColor.RED + "You do not have enough coins to reroll this weapon.");
                    }
                    WeaponManagerMenu.openWeaponEditor(player, databasePlayer, weapon);
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
                (m, e) -> WeaponManagerMenu.openWeaponEditor(player, databasePlayer, weapon)
        );

        menu.openForPlayer(player);
    }

    public static <T extends AbstractWeapon & StatsRerollable> void rerollWeapon(Player player, DatabasePlayer databasePlayer, T weapon) {
        if (weapon == null) {
            return;
        }
        if (databasePlayer.getPveStats().getWeaponInventory().contains(weapon)) {
            TextComponent oldWeapon = new TextComponentBuilder(weapon.getName())
                    .setHoverItem(weapon.generateItemStack())
                    .getTextComponent();
            weapon.reroll();
            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);

            player.spigot().sendMessage(
                    new TextComponent(ChatColor.GRAY + "Reroll Result: "),
                    oldWeapon,
                    new TextComponent(ChatColor.GRAY + " to "),
                    new TextComponentBuilder(weapon.getName())
                            .setHoverItem(weapon.generateItemStack())
                            .getTextComponent()
            );
        }
    }

}
