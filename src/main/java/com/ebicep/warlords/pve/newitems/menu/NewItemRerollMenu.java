package com.ebicep.warlords.pve.newitems.menu;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.PvEUtils;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.newitems.NewItem;
import com.ebicep.warlords.pve.newitems.attributes.NewItemAttribute;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;

public class NewItemRerollMenu {

    public static void open(Player player) {

    }

    private static void reroll(Player player, DatabasePlayer databasePlayer, NewItem item, EnumSet<NewItemAttribute> lockedAttributes) {
        Map<Spendable, Long> cost = new LinkedHashMap<>(item.getSetBonus().rerollCost());
        if (!lockedAttributes.isEmpty()) {
            for (var entry : item.getSetBonus().lockScrollRerollCost().entrySet()) {
                cost.put(entry.getKey(), cost.getOrDefault(entry.getKey(), 0L) + entry.getValue() * lockedAttributes.size());
            }
        }
        for (Map.Entry<Spendable, Long> currenciesLongEntry : cost.entrySet()) {
            Spendable spendable = currenciesLongEntry.getKey();
            long value = currenciesLongEntry.getValue();
            if (spendable.getFromPlayer(databasePlayer) < value) {
                player.sendMessage(Component.text("You need ", NamedTextColor.RED)
                                            .append(spendable.getCostColoredName(value))
                                            .append(Component.text(" to reroll this item!"))
                );
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 2, 0.5f);
                return;
            }
        }
        Menu.openConfirmationMenu(player,
                "Confirm Reroll",
                3,
                new ArrayList<>() {{
                    // TODO
                    addAll(PvEUtils.getCostLore(cost, true));
                }},
                Menu.GO_BACK,
                (m2, e2) -> {
                    Component component = Component.text("You rerolled your item", NamedTextColor.GRAY)
                                                   .append(item.getHoverComponent())
                                                   .append(Component.text(" and it became "));
                    for (Map.Entry<Spendable, Long> spendableLongEntry : cost.entrySet()) {
                        spendableLongEntry.getKey().subtractFromPlayer(databasePlayer, spendableLongEntry.getValue());
                    }
                    item.reroll(lockedAttributes);
                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                    player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_SPLASH, 2, 0.1f);
                    player.closeInventory();
                    AbstractItem.sendItemMessage(player, component.append(item.getHoverComponent()));
                },
                (m2, e2) -> open(player), //TODO
                (m2) -> {
                }
        );
    }

}
