package com.ebicep.warlords.pve.newitems.menu;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.PvEUtils;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.StarPieces;
import com.ebicep.warlords.pve.newitems.NewItem;
import com.ebicep.warlords.pve.newitems.NewItemsUtils;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.java.JavaUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NewItemStarPieceMenu {

    public static void openNewItemStarPieceMenu(Player player, DatabasePlayer databasePlayer, NewItem item, StarPieces selectedStar, Map<Spendable, Long> cost) {
        List<Component> confirmLore = new ArrayList<>();
        confirmLore.addAll(WordWrap.wrap(ComponentBuilder
                                .create()
                                .text("Apply " + (selectedStar.name.startsWith("a") ? "an " : "a "), NamedTextColor.GRAY)
                                .text(selectedStar.name, selectedStar.currency.getTextColor())
                                .text(" star piece to your item. This will override any previous star piece.", NamedTextColor.GRAY)
                                .build(),
                        140
                )
        );
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

                    item.getStarPieceBonuses().add(new NewItem.StarPieceBonus(selectedStar, JavaUtils.randomFromSet(item.getBonusAttributes())));
                    cost.forEach((spendable, amount) -> spendable.subtractFromPlayer(databasePlayer, amount));
                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);

                    NewItemsUtils.sendItemMessage(player, component.append(item.getHoverComponent()).append(Component.text("!")));
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

