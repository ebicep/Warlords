package com.ebicep.warlords.pve.items.menu;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.PvEUtils;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.menu.util.ItemMenuUtil;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ItemModifierMenu {

    private static final LinkedHashMap<Spendable, Long> REROLL_COST = new LinkedHashMap<>() {{
        put(Currencies.SCRAP_METAL, 15L);
    }};

    public static void openItemModifierMenu(Player player, DatabasePlayer databasePlayer, AbstractItem item) {
        Menu menu = new Menu("Reroll Aspect Modifier", 9 * 5);

        ItemMenuUtil.addItemTierRequirement(menu, ItemTier.NONE, item, 1, 1, (m, e) -> {
            ItemCraftingMenu.openItemSelectMenu(
                    player,
                    databasePlayer,
                    null,
                    (m2, e2) -> openItemModifierMenu(player, databasePlayer, item),
                    (i2, m2, e2) -> {
                        openItemModifierMenu(player, databasePlayer, i2);
                    }
            );
        });
        ItemMenuUtil.addSpendableCostRequirement(databasePlayer, menu, REROLL_COST, 1, 2);
        ItemMenuUtil.addItemConfirmation(menu, () -> {
            boolean requirementsMet = item != null;
            boolean enoughCurrency = REROLL_COST
                    .entrySet()
                    .stream()
                    .allMatch(entry -> entry.getKey().getFromPlayer(databasePlayer) >= entry.getValue());
            ItemBuilder itemBuilder = new ItemBuilder(Material.CHIPPED_ANVIL)
                    .name(Component.text("Reroll Aspect Modifier", NamedTextColor.GREEN))
                    .lore(
                            ItemMenuUtil.getRequirementMetString(requirementsMet, "Item Selected"),
                            ItemMenuUtil.getRequirementMetString(enoughCurrency, "Enough Scrap Metal"),
                            Component.empty()
                    );
            menu.setItem(6, 2,
                    itemBuilder.get(),
                    (m, e) -> {
                        if (!requirementsMet) {
                            player.sendMessage(Component.text("No Item selected!", NamedTextColor.RED));
                            return;
                        }
                        for (Map.Entry<Spendable, Long> currenciesLongEntry : REROLL_COST.entrySet()) {
                            Spendable spendable = currenciesLongEntry.getKey();
                            Long cost = currenciesLongEntry.getValue();
                            if (spendable.getFromPlayer(databasePlayer) < cost) {
                                player.sendMessage(Component.text("You need ", NamedTextColor.RED)
                                                            .append(spendable.getCostColoredName(cost))
                                                            .append(Component.text(" to reroll the Aspect Modifier of this item!"))
                                );
                                return;
                            }
                        }

                        Menu.openConfirmationMenu(player,
                                "Confirm Reroll",
                                3,
                                new ArrayList<>() {{
                                    add(Component.text("Reroll Aspect Modifier", NamedTextColor.GRAY));
                                    addAll(PvEUtils.getCostLore(REROLL_COST, true));
                                }},
                                Menu.GO_BACK,
                                (m2, e2) -> {
                                    for (Map.Entry<Spendable, Long> currenciesLongEntry : REROLL_COST.entrySet()) {
                                        currenciesLongEntry.getKey().subtractFromPlayer(databasePlayer, currenciesLongEntry.getValue());
                                    }
                                    Component oldHover = item.getHoverComponent();
                                    item.applyRandomModifierOnly();
                                    Component newHover = item.getHoverComponent();
                                    AbstractItem.sendItemMessage(player, Component.textOfChildren(
                                            Component.text("You rerolled the Aspect Modifier of ", NamedTextColor.GRAY),
                                            oldHover,
                                            Component.text(" and it became ", NamedTextColor.GRAY),
                                            newHover,
                                            Component.text("!", NamedTextColor.GRAY)
                                    ));
                                    player.playSound(player.getLocation(), "mage.inferno.activation", 2, 0.75f);
                                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2, 1);
                                    player.closeInventory();
                                },
                                (m2, e2) -> ItemCraftingMenu.openItemCraftingMenu(player, databasePlayer),
                                (m2) -> {
                                }
                        );
                    }
            );
        });

        menu.setItem(4, 4, Menu.MENU_BACK, (m, e) -> ItemCraftingMenu.openItemCraftingMenu(player, databasePlayer));
        menu.addBorder(Menu.GRAY_EMPTY_PANE, true);
        menu.openForPlayer(player);
    }

}
