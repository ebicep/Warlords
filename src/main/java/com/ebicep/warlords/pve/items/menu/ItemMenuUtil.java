package com.ebicep.warlords.pve.items.menu;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.mobs.MobDrops;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class ItemMenuUtil {

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

    public static void addMobDropRequirement(
            DatabasePlayer databasePlayer,
            Menu menu,
            LinkedHashMap<MobDrops, Long> cost,
            int x,
            int y
    ) {
        List<String> costLore = new ArrayList<>() {{
            add("");
            add(ChatColor.AQUA + "Drops Cost: ");
            cost.forEach((currencies, amount) -> add(ChatColor.GRAY + " - " + currencies.getCostColoredName(amount)));
        }};
        DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
        menu.setItem(x, y,
                new ItemBuilder(Material.BOOK)
                        .name(ChatColor.GREEN + "Mob Drops")
                        .lore(Arrays.stream(MobDrops.VALUES)
                                    .map(drop -> drop.getCostColoredName(databasePlayer.getPveStats()
                                                                                       .getMobDrops()
                                                                                       .getOrDefault(drop, 0L)))
                                    .collect(Collectors.joining("\n")))
                        .addLore(costLore)
                        .get(),
                (m, e) -> {
                }
        );
        boolean hasRequiredDrops = cost
                .entrySet()
                .stream()
                .allMatch(mobDropsLongEntry -> pveStats.getMobDrops(mobDropsLongEntry.getKey()) >= mobDropsLongEntry.getValue());
        menu.setItem(x + 1, y,
                new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) (hasRequiredDrops ? 5 : 14))
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
