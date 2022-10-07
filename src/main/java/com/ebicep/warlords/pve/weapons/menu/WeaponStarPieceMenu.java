package com.ebicep.warlords.pve.weapons.menu;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.StarPieces;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.util.bukkit.TextComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
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
                    TextComponent weaponBefore = new TextComponentBuilder(weapon.getName())
                            .setHoverItem(weapon.generateItemStack())
                            .getTextComponent();
                    weapon.setStarPiece(starPieceCurrency, weapon.generateRandomStatBonus());
                    weapon.getStarPieceBonusCost(starPieceCurrency).forEach(databasePlayerPvE::subtractCurrency);
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
