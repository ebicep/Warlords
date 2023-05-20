package com.ebicep.warlords.pve.weapons.menu;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.StarPieces;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WeaponStarPieceMenu {

    public static void openWeaponStarPieceMenu(Player player, DatabasePlayer databasePlayer, AbstractLegendaryWeapon weapon, StarPieces starPieceCurrency) {
        DatabasePlayerPvE databasePlayerPvE = databasePlayer.getPveStats();

        List<Component> confirmLore = new ArrayList<>();
        confirmLore.add(Component.text("Apply a star piece to your weapon.", NamedTextColor.GRAY));
        confirmLore.add(Component.text("This will override any previous star piece.", NamedTextColor.GRAY));
        confirmLore.addAll(weapon.getStarPieceCostLore(starPieceCurrency));
        Menu.openConfirmationMenu(
                player,
                "Confirm Star Piece Application",
                3,
                confirmLore,
                Menu.GO_BACK,
                (m2, e2) -> {
                    Component component = Component.text("You applied a star piece onto ", NamedTextColor.GRAY)
                                                   .append(weapon.getHoverComponent(false))
                                                   .append(Component.text(" and it became "));

                    weapon.setStarPiece(starPieceCurrency, weapon.generateRandomStatBonus());
                    weapon.getStarPieceBonusCost(starPieceCurrency).forEach(databasePlayerPvE::subtractCurrency);
                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);

                    player.sendMessage(component.append(weapon.getHoverComponent(false))
                                                .append(Component.text("!"))
                    );

                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 500, 2);

                    WeaponManagerMenu.openWeaponEditor(player, databasePlayer, weapon);
                },
                (m2, e2) -> WeaponManagerMenu.openWeaponEditor(player, databasePlayer, weapon),
                (m2) -> {
                    m2.setItem(4, 1,
                            weapon.generateItemStack(false),
                            (m, e) -> {
                            }
                    );
                }
        );
    }
}
