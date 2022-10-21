package com.ebicep.warlords.pve.weapons.menu;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.StarPieces;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WeaponStarPieceMenu {

    public static void openWeaponStarPieceMenu(Player player, DatabasePlayer databasePlayer, AbstractLegendaryWeapon weapon, StarPieces starPieceCurrency) {
        DatabasePlayerPvE databasePlayerPvE = databasePlayer.getPveStats();

        List<String> confirmLore = new ArrayList<>();
        confirmLore.add(ChatColor.GRAY + "Apply a star piece to your weapon.");
        confirmLore.add(ChatColor.GRAY + "This will override any previous star piece.");
        confirmLore.addAll(weapon.getStarPieceCostLore(starPieceCurrency));
        Menu.openConfirmationMenu(
                player,
                "Confirm Star Piece Application",
                3,
                confirmLore,
                Collections.singletonList(ChatColor.GRAY + "Go back"),
                (m2, e2) -> {
                    ComponentBuilder componentBuilder = new ComponentBuilder(ChatColor.GRAY + "You applied a star piece onto ")
                            .appendHoverItem(weapon.getName(), weapon.generateItemStack())
                            .append(ChatColor.GRAY + " and it became ");

                    weapon.setStarPiece(starPieceCurrency, weapon.generateRandomStatBonus());
                    weapon.getStarPieceBonusCost(starPieceCurrency).forEach(databasePlayerPvE::subtractCurrency);
                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);

                    player.spigot().sendMessage(
                            componentBuilder
                                    .appendHoverItem(weapon.getName(), weapon.generateItemStack())
                                    .append(ChatColor.GRAY + "!")
                                    .create()
                    );

                    WeaponManagerMenu.openWeaponEditor(player, databasePlayer, weapon);
                },
                (m2, e2) -> WeaponManagerMenu.openWeaponEditor(player, databasePlayer, weapon),
                (m2) -> {
                    m2.setItem(4, 1,
                            weapon.generateItemStack(),
                            (m, e) -> {
                            }
                    );
                }
        );
    }
}
