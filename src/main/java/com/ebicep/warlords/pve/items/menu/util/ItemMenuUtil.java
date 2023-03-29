package com.ebicep.warlords.pve.items.menu.util;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.PvEUtils;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.BiConsumer;

public class ItemMenuUtil {

    public static String getRequirementMetString(boolean requirementMet, String requirement) {
        return (requirementMet ? ChatColor.GREEN + "✔" : ChatColor.RED + "✖") + ChatColor.GRAY + " " + requirement;
    }

    public static void addItemTierRequirement(
            Menu menu,
            ItemTier tier,
            AbstractItem<?, ?, ?> item,
            int x,
            int y,
            BiConsumer<Menu, InventoryClickEvent> onClick
    ) {
        ItemBuilder itemBuilder;
        if (item == null) {
            itemBuilder = new ItemBuilder(tier.clayBlock)
                    .name(ChatColor.GREEN + "Click to Select Item");
        } else {
            itemBuilder = item.generateItemBuilder()
                              .addLore(
                                      "",
                                      ChatColor.YELLOW.toString() + ChatColor.BOLD + "CLICK" + ChatColor.GREEN + " to swap this item"
                              );
        }
        menu.setItem(x, y,
                itemBuilder.get(),
                onClick
        );
        addPaneRequirement(menu, x + 1, y, item != null);
    }

    public static void addPaneRequirement(Menu menu, int x, int y, boolean requirementMet) {
        menu.setItem(x, y,
                new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) (requirementMet ? 5 : 14))
                        .name(" ")
                        .get(),
                (m, e) -> {
                }
        );
    }

    public static void addSpendableCostRequirement(
            DatabasePlayer databasePlayer,
            Menu menu,
            LinkedHashMap<Spendable, Long> cost,
            int x,
            int y
    ) {
        List<String> costLore = PvEUtils.getCostLore(cost);
        menu.setItem(x, y,
                new ItemBuilder(Material.BOOK)
                        .name(ChatColor.GREEN + "Cost")
                        .lore(costLore)
                        .get(),
                (m, e) -> {
                }
        );
        boolean hasRequiredCosts = cost
                .entrySet()
                .stream()
                .allMatch(spendableLongEntry -> spendableLongEntry.getKey().getFromPlayer(databasePlayer) >= spendableLongEntry.getValue());
        menu.setItem(x + 1, y,
                new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) (hasRequiredCosts ? 5 : 14))
                        .name(" ")
                        .get(),
                (m, e) -> {
                }
        );
    }

    public static void addItemConfirmation(
            Menu menu,
            Runnable onCenterClick
    ) {
        for (int i = 6; i < 9; i++) {
            for (int j = 0; j < 3; j++) {
                if (i == 7 && j == 1) {
                    onCenterClick.run();
                } else {
                    menu.setItem(i, j,
                            new ItemBuilder(Material.IRON_FENCE)
                                    .name(" ")
                                    .get(),
                            (m, e) -> {
                            }
                    );
                }
            }
        }
    }

}
