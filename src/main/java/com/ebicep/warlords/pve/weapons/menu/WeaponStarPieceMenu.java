package com.ebicep.warlords.pve.weapons.menu;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.weapons.AbstractTierOneWeapon;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.TextComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class WeaponStarPieceMenu {

    public static void openWeaponStarPieceMenu(Player player, DatabasePlayer databasePlayer, AbstractTierOneWeapon weapon) {
        DatabasePlayerPvE databasePlayerPvE = databasePlayer.getPveStats();

        Menu menu = new Menu("Confirm Star Piece Application", 9 * 3);

        menu.setItem(2, 1,
                new ItemBuilder(Material.STAINED_CLAY, 1, (short) 13)
                        .name(ChatColor.GREEN + "Confirm")
                        .lore(
                                ChatColor.GRAY + "Apply a star piece to your weapon.",
                                ChatColor.GRAY + "This will override any previous star piece.",
                                "",
                                ChatColor.GRAY + "Cost: " + ChatColor.WHITE + weapon.getStarPieceBonusCost() + " Synthetic Shards",
                                "",
                                ChatColor.RED + "WARNING: " + ChatColor.GRAY + "This action cannot be undone."
                        )
                        .get(),
                (m, e) -> {
                    if (databasePlayerPvE.getCurrencyValue(Currencies.SYNTHETIC_SHARD) < weapon.getStarPieceBonusCost()) {
                        player.sendMessage(ChatColor.RED + "You do not have enough synthetic shards to apply this star piece.");
                    } else {
                        TextComponent weaponBefore = new TextComponentBuilder(weapon.getName())
                                .setHoverItem(weapon.generateItemStack())
                                .getTextComponent();
                        databasePlayerPvE.subtractOneCurrency(weapon.getRarity().starPieceCurrency);
                        databasePlayerPvE.subtractCurrency(Currencies.SYNTHETIC_SHARD, weapon.getStarPieceBonusCost());
                        weapon.setStarPieceBonus();
                        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);

                        TextComponent weaponAfter = new TextComponentBuilder(weapon.getName())
                                .setHoverItem(weapon.generateItemStack())
                                .getTextComponent();
                        player.spigot().sendMessage(
                                new TextComponent(ChatColor.GRAY + "You applied a star piece onto "),
                                weaponBefore,
                                new TextComponent(ChatColor.GRAY + " and it became "),
                                weaponAfter,
                                new TextComponent(ChatColor.GRAY + "!")
                        );

                        WeaponManagerMenu.openWeaponEditor(player, databasePlayer, weapon);
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
                (m, e) -> WeaponManagerMenu.openWeaponEditor(player, databasePlayer, weapon)
        );

        menu.openForPlayer(player);
    }
}
