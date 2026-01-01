package com.ebicep.warlords.pve.newitems.menu;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.PvEUtils;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.newitems.NewItem;
import com.ebicep.warlords.pve.newitems.NewItemsManager;
import com.ebicep.warlords.pve.newitems.NewItemsUtils;
import com.ebicep.warlords.pve.newitems.tiers.NewItemTier;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NewItemCraftMenu {

    public static void open(Player player) {
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);
        Menu menu = new Menu("Craft New Item", 9 * 4);

        int x = 2;
        for (NewItemTier tier : NewItemTier.VALUES) {
            Map<Spendable, Long> craftCost = tier.getCraftCost();
            if (craftCost == null || craftCost.isEmpty()) {
                continue;
            }

            Material icon = tier.getTerracotaMaterial() != null ? tier.getTerracotaMaterial() : Material.BOOK;
            menu.setItem(x, 1, new ItemBuilder(icon)
                            .name(Component.text(tier.getName(), tier.getTextColor()))
                            .lore(PvEUtils.getCostLore(craftCost, null, false))
                            .get(),
                    (m, e) -> handleClick(player, databasePlayer, tier, craftCost)
            );
            x += 2;
        }

        menu.setItem(4, 3, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    private static void handleClick(Player player, DatabasePlayer databasePlayer, NewItemTier tier, Map<Spendable, Long> craftCost) {
        for (Map.Entry<Spendable, Long> entry : craftCost.entrySet()) {
            Spendable spendable = entry.getKey();
            long amount = entry.getValue();
            if (spendable.getFromPlayer(databasePlayer) < amount) {
                player.sendMessage(Component.text("You need ", NamedTextColor.RED)
                                            .append(spendable.getCostColoredName(amount))
                                            .append(Component.text(" to craft this item!")));
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 2, 0.5f);
                return;
            }
        }

        List<Component> confirmLore = new ArrayList<>();
        confirmLore.add(Component.text("Tier: ", NamedTextColor.GRAY).append(Component.text(tier.getName(), tier.getTextColor())));
        confirmLore.addAll(PvEUtils.getCostLore(craftCost, "Cost", true));

        Menu.openConfirmationMenu(player,
                "Confirm Craft",
                3,
                confirmLore,
                Menu.GO_BACK,
                (m2, e2) -> {
                    for (Map.Entry<Spendable, Long> entry : craftCost.entrySet()) {
                        entry.getKey().subtractFromPlayer(databasePlayer, entry.getValue());
                    }
                    NewItem item = NewItemsUtils.generateRandomItem(tier);
                    NewItemsManager itemsManager = databasePlayer.getPveStats().getNewItemsManager();
                    itemsManager.addItem(item);
                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                    NewItem.sendItemMessage(player, Component.text("You crafted ", NamedTextColor.GRAY).append(item.getHoverComponent()));
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2, 1);
                    player.closeInventory();
                },
                (m2, e2) -> open(player),
                (m2) -> {}
        );
    }

}
