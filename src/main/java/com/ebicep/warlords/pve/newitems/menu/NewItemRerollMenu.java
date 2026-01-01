package com.ebicep.warlords.pve.newitems.menu;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.menu.MenuUtils;
import com.ebicep.warlords.pve.PvEUtils;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.newitems.NewItem;
import com.ebicep.warlords.pve.newitems.NewItemLoreCreator;
import com.ebicep.warlords.pve.newitems.NewItemRerollCost;
import com.ebicep.warlords.pve.newitems.attributes.NewItemAttribute;
import com.ebicep.warlords.pve.newitems.setbonus.NewItemsSetBonus;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.java.StringUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static com.ebicep.warlords.menu.Menu.*;

public class NewItemRerollMenu {

    public static void open(Player player) {
        open(player, null, EnumSet.noneOf(NewItemAttribute.class));
    }

    private static void open(Player player, NewItem item, EnumSet<NewItemAttribute> lockedAttributes) {
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);
        Menu menu = new Menu("Reroll Item", 9 * 5);

        menu.setItem(4, 0,
                new ItemBuilder(Material.BOOK)
                        .name(Component.text("Information", NamedTextColor.GREEN))
                        .lore(WordWrap.wrap(Component.text(
                                        "You may reroll an item's attribute at a certain cost. Each reroll increase the cost of you next attempt. Up to " + NewItemRerollCost.MAX_REROLLS + " attempts.",
                                        NamedTextColor.GRAY
                                ), 140
                        ))
                        .get(),
                (m, e) -> {
                }
        );
        BiConsumer<Menu, InventoryClickEvent> selectItemToReroll = (m, e) -> {
            NewItemSearchMenu itemSearchMenu = new NewItemSearchMenu(
                    player,
                    "Select Item to Reroll",
                    (i, m2, e2) -> {
                        open(player, i, lockedAttributes);
                    },
                    builder -> builder,
                    new NewItemSearchMenu.PlayerItemMenuSettings(databasePlayer)
                            .setItemInventory(databasePlayer.getPveStats()
                                                            .getNewItemsManager()
                                                            .getItemInventory()
                                                            .stream()
                                                            .filter(i -> i.getRerollCostsHistory().size() < NewItemRerollCost.MAX_REROLLS)
                                                            .collect(Collectors.toList())),
                    databasePlayer
            );
            itemSearchMenu.open();
        };
        if (item == null) {
            menu.setItem(4, 2,
                    new ItemBuilder(Material.WHITE_TERRACOTTA)
                            .name(Component.text("Click to Select Item", NamedTextColor.GREEN))
                            .get(),
                    selectItemToReroll
            );
        } else {
            NewItemsSetBonus setBonus = item.getSetBonus();
            menu.setItem(2, 2,
                    item.getItemBuilder()
                        .addLore(
                                Component.empty(),
                                Component.textOfChildren(
                                        Component.text("CLICK", NamedTextColor.YELLOW, TextDecoration.BOLD),
                                        Component.text(" to swap this item", NamedTextColor.GREEN)
                                )
                        )
                        .get(),
                    selectItemToReroll
            );
            menu.setItem(3, 2,
                    new ItemBuilder(Material.REPEATER)
                            .name(Component.text("Set Bonus Attribute Ranges", NamedTextColor.GREEN))
                            .lore(new NewItemLoreCreator.Builder(setBonus)
                                    .addBonusAttributes(false, item.getBonusAttributes())
                                    .build())
                            .get(),
                    ACTION_DO_NOTHING
            );
            menu.setItem(4, 2,
                    new ItemBuilder(Material.TRIPWIRE_HOOK)
                            .name(Component.text("Locked Attributes", NamedTextColor.GREEN))
                            .lore(
                                    lockedAttributes.isEmpty() ?
                                    List.of(Component.text(" - None", NamedTextColor.GRAY)) :
                                    lockedAttributes.stream()
                                                    .map(value ->
                                                            Component.text(" - ", NamedTextColor.GRAY).append(
                                                                    Component.text(value.getName(), value.getTextColor()))
                                                    )
                                                    .collect(Collectors.toList())
                            )
                            .addLore(
                                    Component.empty(),
                                    Component.textOfChildren(
                                            Component.text("CLICK", NamedTextColor.YELLOW, TextDecoration.BOLD),
                                            Component.text(" to select locked attributes", NamedTextColor.GREEN)
                                    )
                            )
                            .get(),
                    (m, e) -> {
                        MenuUtils.openEnumSelectorMenu(
                                player,
                                "Select Locked Attributes",
                                item.getBonusAttributes().toArray(new NewItemAttribute[0]),
                                lockedAttributes,
                                NewItemRerollCost.MAX_LOCKED_ATTRIBUTES,
                                NewItemAttribute::getItemStack,
                                (m2, e2) -> open(player, item, lockedAttributes),
                                selectorMenu -> {
                                    selectorMenu.setItem(4, 0,
                                            new ItemBuilder(Material.BOOK)
                                                    .name(Component.text("Information", NamedTextColor.GREEN))
                                                    .lore(WordWrap.wrap(Component.text(
                                                            "You can only select " + NewItemRerollCost.MAX_LOCKED_ATTRIBUTES +
                                                                    StringUtils.toPlural(" attribute", NewItemRerollCost.MAX_LOCKED_ATTRIBUTES) +
                                                                    " to lock per reroll. Locked attributes will not change when you reroll the item.",
                                                                    NamedTextColor.GRAY
                                                            ), 140
                                                    ))
                                                    .get(),
                                            ACTION_DO_NOTHING
                                    );
                                }
                        );
                    }
            );
            List<Map<Spendable, Long>> rerollCostsHistory = item.getRerollCostsHistory();
            int newRerollCount = rerollCostsHistory.size() + 1;
            Map<Spendable, Long> cost = new LinkedHashMap<>(item.getTier().getRerollCost().getOrDefault(newRerollCount, Collections.emptyMap()));
            cost.putAll(item.getSetBonus().getRerollCost().getOrDefault(newRerollCount, Collections.emptyMap()));
            if (!lockedAttributes.isEmpty()) {
                Map<Spendable, Long> lockScrollRerollMap = new LinkedHashMap<>(item.getTier().getLockScrollRerollCost().getOrDefault(newRerollCount, Collections.emptyMap()));
                lockScrollRerollMap.putAll(item.getSetBonus().getLockScrollRerollCost().getOrDefault(newRerollCount, Collections.emptyMap()));
                if (lockScrollRerollMap.isEmpty()) {
                    ChatUtils.MessageType.NEW_ITEMS.sendErrorMessage("No lock scroll reroll cost found for reroll count " + newRerollCount + ", " + item.getSetBonus());
                } else {
                    for (var entry : lockScrollRerollMap.entrySet()) {
                        cost.put(entry.getKey(), cost.getOrDefault(entry.getKey(), 0L) + entry.getValue() * lockedAttributes.size());
                    }
                }
            }
            menu.setItem(5, 2,
                    new ItemBuilder(Material.CHEST)
                            .name(Component.text("Reroll Cost", NamedTextColor.GREEN))
                            .lore(PvEUtils.getCostLore(cost, null, false))
                            .get(),
                    ACTION_DO_NOTHING
            );
            menu.setItem(6, 2,
                    new ItemBuilder(Material.ENCHANTING_TABLE)
                            .name(Component.text("Reroll", NamedTextColor.GREEN))
                            .lore(Component.text("Current Attempts: ", NamedTextColor.GRAY)
                                           .append(Component.text(rerollCostsHistory.size() + "/" + NewItemRerollCost.MAX_REROLLS, NamedTextColor.YELLOW))
                            )
                            .get(),
                    (m, e) -> {
                        if (cost.isEmpty()) {
                            player.sendMessage(Component.text("This item cannot be rerolled!", NamedTextColor.RED));
                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 2, 0.5f);
                            player.closeInventory();
                            return;
                        }
                        reroll(player, databasePlayer, item, lockedAttributes, cost);
                    }
            );
        }

        menu.setItem(4, 4, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    private static void reroll(Player player, DatabasePlayer databasePlayer, NewItem item, EnumSet<NewItemAttribute> lockedAttributes, Map<Spendable, Long> cost) {
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
        NewItemsSetBonus setBonus = item.getSetBonus();
        List<Component> confirmLore = new ArrayList<>();
        confirmLore.add(Component.text("Attempt: ", NamedTextColor.GRAY).append(
                Component.text(item.getRerollCostsHistory().size() + 1 + "/" + NewItemRerollCost.MAX_REROLLS, NamedTextColor.YELLOW)
        ));
        confirmLore.add(Component.empty());
        confirmLore.add(Component.text("Current values:", NamedTextColor.GRAY));
        for (NewItemAttribute bonusAttribute : NewItemAttribute.BONUS_ATTRIBUTES) {
            Integer value = item.getBonusAttributeValues().get(bonusAttribute);
            if (value == null) {
                continue;
            }
            Pair<Float, Float> defaultRange = setBonus.getTier().getBonusAttributeRanges().getOrDefault(bonusAttribute, NewItemLoreCreator.ZERO_RANGE);
            Pair<Float, Float> range = setBonus.getBonusAttributeRanges().getOrDefault(bonusAttribute, defaultRange);
            float high = range.getA() != 0 ? range.getB() : defaultRange.getB();
            Component component = bonusAttribute.formatValue(value, "+");
            if ((int) Math.ceil(high) == value) {
                component = component.append(Component.text(" [MAX]", NamedTextColor.LIGHT_PURPLE));
            }
            if (lockedAttributes.contains(bonusAttribute)) {
                component = component.append(Component.text(" [LOCKED]", NamedTextColor.RED));
            }
            confirmLore.add(component);
        }
        confirmLore.add(Component.empty());
        confirmLore.add(Component.text("Possible value ranges:", NamedTextColor.GRAY));
        confirmLore.addAll(new NewItemLoreCreator.Builder(item)
                .addBonusAttributes(false, item.getBonusAttributes())
                .build());
        confirmLore.add(Component.empty());
        confirmLore.add(Component.text("Locked Attributes:", NamedTextColor.GRAY));
        if (lockedAttributes.isEmpty()) {
            confirmLore.add(Component.text(" - None", NamedTextColor.GRAY));
        } else {
            for (NewItemAttribute lockedAttribute : lockedAttributes) {
                confirmLore.add(Component.text(" - ", NamedTextColor.GRAY).append(
                        Component.text(lockedAttribute.getName(), lockedAttribute.getTextColor())
                ));
            }
        }
        confirmLore.addAll(PvEUtils.getCostLore(cost, "Reroll Cost", true));
        Menu.openConfirmationMenu(player,
                "Confirm Reroll",
                3,
                confirmLore,
                Menu.GO_BACK,
                (m2, e2) -> {
                    Component component = Component.text("You rerolled your item ", NamedTextColor.GRAY)
                                                   .append(item.getHoverComponent())
                                                   .append(Component.text(" and it became "));
                    for (Map.Entry<Spendable, Long> spendableLongEntry : cost.entrySet()) {
                        spendableLongEntry.getKey().subtractFromPlayer(databasePlayer, spendableLongEntry.getValue());
                    }
                    item.reroll(lockedAttributes);
                    item.getRerollCostsHistory().add(cost);
                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                    player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_SPLASH, 2, 0.1f);
                    player.closeInventory();
                    AbstractItem.sendItemMessage(player, component.append(item.getHoverComponent()));
                },
                (m2, e2) -> open(player, item, lockedAttributes),
                (m2) -> {
                }
        );
    }

}
