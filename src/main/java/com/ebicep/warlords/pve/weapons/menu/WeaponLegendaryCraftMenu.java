package com.ebicep.warlords.pve.weapons.menu;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryWeapon;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.TextComponentBuilder;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;

public class WeaponLegendaryCraftMenu {

    public static void openWeaponLegendaryCraftMenu(Player player, DatabasePlayer databasePlayer) {
        Menu menu = new Menu("Craft Legendary Weapon", 9 * 6);

        menu.setItem(4, 3,
                new ItemBuilder(Material.DIAMOND_SWORD)
                        .name(ChatColor.GREEN + "Craft Legendary Weapon")
                        .lore(
                                ChatColor.AQUA + "Craft Cost: ",
                                ChatColor.GRAY + " - " + ChatColor.GREEN + "?" + Currencies.COIN.getColoredName()
                        )
                        .get(),
                (m, e) -> {
                    DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
                    if (pveStats.getCurrencyValue(Currencies.COIN) < 0) {
                        player.sendMessage(ChatColor.RED + "You do not have enough " + Currencies.COIN.getColoredName() +
                                ChatColor.RED + " to craft a legendary weapon.");
                        return;
                    }
                    Menu.openConfirmationMenu(
                            player,
                            "Craft Legendary Weapon",
                            3,
                            Arrays.asList(
                                    ChatColor.GRAY + "Craft a Legendary Weapon for",
                                    ChatColor.GREEN + "?" + Currencies.COIN.getColoredName()
                            ),
                            Collections.singletonList(ChatColor.GRAY + "Go back"),
                            (m2, e2) -> {
                                LegendaryWeapon weapon = new LegendaryWeapon(player.getUniqueId());
                                pveStats.subtractCurrency(Currencies.COIN, 0);
                                pveStats.getWeaponInventory().add(weapon);
                                player.spigot().sendMessage(
                                        new TextComponent(ChatColor.GRAY + "Crafted Legendary Weapon: "),
                                        new TextComponentBuilder(weapon.getName())
                                                .setHoverItem(weapon.generateItemStack())
                                                .getTextComponent()
                                );
                                DatabaseManager.queueUpdatePlayerAsync(databasePlayer);

                            },
                            (m2, e2) -> openWeaponLegendaryCraftMenu(player, databasePlayer),
                            (m2) -> {
                            }
                    );
                }
        );

        menu.setItem(4, 5, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

}
