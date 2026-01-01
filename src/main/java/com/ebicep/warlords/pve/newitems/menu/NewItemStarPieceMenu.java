package com.ebicep.warlords.pve.newitems.menu;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.PvEUtils;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.newitems.NewItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NewItemStarPieceMenu {

    public static void openNewItemStarPieceMenu(Player player, DatabasePlayer databasePlayer, NewItem item, Map<Spendable, Long> cost) {
        List<Component> confirmLore = new ArrayList<>();
        confirmLore.add(Component.text("Apply a star piece to your item.", NamedTextColor.GRAY));
        confirmLore.add(Component.text("This will override any previous star piece.", NamedTextColor.GRAY));
        confirmLore.addAll(PvEUtils.getCostLore(cost, true));

        Menu.openConfirmationMenu(
                player,
                "Confirm Star Piece Application",
                3,
                confirmLore,
                Menu.GO_BACK,
                (m2, e2) -> {
                    Component component = Component.text("You applied a star piece onto ", NamedTextColor.GRAY)
                                                   .append(item.getHoverComponent())
                                                   .append(Component.text(" and it became "));

                    // TODO apply star piece effect to item
                    cost.forEach((spendable, amount) -> spendable.subtractFromPlayer(databasePlayer, amount));
                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);

                    NewItem.sendItemMessage(player, component.append(item.getHoverComponent()).append(Component.text("!")));
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 500, 2);

                    NewItemEditorMenu.open(player, item);
                },
                (m2, e2) -> NewItemEditorMenu.open(player, item),
                (m2) -> {
                    m2.setItem(4, 1,
                            item.getItemBuilder().get(),
                            (m, e) -> {
                            }
                    );
                }
        );
    }

}

