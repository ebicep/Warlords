package com.ebicep.warlords.pve.items.menu;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.PvEUtils;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.ItemsManager;
import com.ebicep.warlords.pve.items.events.ItemCraftEvent;
import com.ebicep.warlords.pve.items.menu.util.ItemMenuUtil;
import com.ebicep.warlords.pve.items.menu.util.ItemSearchMenu;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AbstractFixedItem;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.items.types.AbstractSpecialItem;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;
import com.ebicep.warlords.pve.mobs.MobDrop;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.java.TriConsumer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static com.ebicep.warlords.menu.Menu.MENU_BACK;

public class ItemCraftingMenu {

    private static final HashMap<ItemTier, TierCostInfo> TIER_COST_INFO = new HashMap<>() {{
        put(ItemTier.DELTA, new TierCostInfo(
                new LinkedHashMap<>() {{
                    put(Currencies.SYNTHETIC_SHARD, 7_500L);
                    put(Currencies.LEGEND_FRAGMENTS, 1_000L);
                    put(Currencies.SCRAP_METAL, 50L);
                    put(MobDrop.ZENITH_STAR, 2L);
                }},
                new Pair<>(1, 4),
                new ArrayList<>() {{
                    add(new TierRequirement(ItemTier.ALPHA, 1, 1));
                    add(new TierRequirement(ItemTier.BETA, 1, 2));
                    add(new TierRequirement(ItemTier.GAMMA, 1, 3));
                }}
        ));
        put(ItemTier.OMEGA, new TierCostInfo(
                new LinkedHashMap<>() {{
                    put(Currencies.SYNTHETIC_SHARD, 15_000L);
                    put(Currencies.LEGEND_FRAGMENTS, 5_000L);
                    put(Currencies.SCRAP_METAL, 100L);
                    put(MobDrop.ZENITH_STAR, 8L);
                }},
                new Pair<>(1, 2),
                new ArrayList<>() {{
                    add(new TierRequirement(ItemTier.DELTA, 1, 1));
                }}
        ));
    }};
    private static final LinkedHashMap<Spendable, Long> CELESTIAL_SMELTERY_COST = new LinkedHashMap<>() {{
        put(Currencies.LEGEND_FRAGMENTS, 5000L);
        put(Currencies.SCRAP_METAL, 100L);
        put(MobDrop.ZENITH_STAR, 3L);
    }};

    public static void openItemCraftingMenu(Player player, DatabasePlayer databasePlayer) {
        Menu menu = new Menu("Ethical Enya", 9 * 4);

        menu.setItem(2, 1,
                new ItemBuilder(Material.CHIPPED_ANVIL)
                        .name(Component.text("Reroll Aspect Modifier", NamedTextColor.GREEN))
                        .lore(WordWrap.wrap(
                                Component.text(
                                        "Every Item comes with 0, 1, or 2 Aspect Modifiers. The modifier bonus depends on its type and gives a small bonus against mobs of its Aspect. Items with 2 modifiers has its bonus reduced by half.",
                                        NamedTextColor.GRAY
                                ),
                                170
                        ))
                        .addLore(
                                Component.empty(),
                                Component.textOfChildren(
                                        Component.text("Gauntlets", NamedTextColor.DARK_GREEN),
                                        Component.text(" increase damage done.", NamedTextColor.GRAY)
                                ),
                                Component.textOfChildren(
                                        Component.text("Tomes", NamedTextColor.DARK_GREEN),
                                        Component.text(" negates the effect of the Aspect.", NamedTextColor.GRAY)
                                ),
                                Component.textOfChildren(
                                        Component.text("Bucklers", NamedTextColor.DARK_GREEN),
                                        Component.text(" decrease damage taken.", NamedTextColor.GRAY)
                                )
                        )
                        .get(),
                (m, e) -> ItemModifierMenu.openItemModifierMenu(player, databasePlayer, null)
        );

        menu.setItem(4, 1,
                new ItemBuilder(Material.YELLOW_TERRACOTTA)
                        .name(Component.text("Delta Forging", NamedTextColor.GREEN))
                        .lore(Component.text("Craft a Delta Tiered Item", NamedTextColor.GRAY))
                        .get(),
                (m, e) -> openForgingMenu(player, databasePlayer, ItemTier.DELTA, new HashMap<>())
        );

        menu.setItem(6, 1,
                new ItemBuilder(Material.WHITE_TERRACOTTA)
                        .name(Component.text("Omega Forging", NamedTextColor.GREEN))
                        .lore(Component.text("Craft an Omega Tiered Item", NamedTextColor.GRAY))
                        .get(),
                (m, e) -> openForgingMenu(player, databasePlayer, ItemTier.OMEGA, new HashMap<>())
        );

        menu.setItem(4, 3, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    private static void openForgingMenu(Player player, DatabasePlayer databasePlayer, ItemTier itemTier, HashMap<ItemTier, AbstractItem> items) {
        Menu menu = new Menu(itemTier.name + " Forging", 9 * 6);

        TierCostInfo tierCostInfo = TIER_COST_INFO.get(itemTier);
        List<TierRequirement> requirements = tierCostInfo.requirements();
        for (TierRequirement requirement : requirements) {
            ItemTier tier = requirement.tier();
            ItemMenuUtil.addItemTierRequirement(menu, tier, items.get(tier), requirement.x(), requirement.y(), (m, e) -> {
                openItemSelectMenu(
                        player,
                        databasePlayer,
                        tier,
                        (m2, e2) -> openForgingMenu(player, databasePlayer, itemTier, items),
                        (i2, m2, e2) -> {
                            items.put(tier, i2);
                            openForgingMenu(player, databasePlayer, itemTier, items);
                        }
                );
            });
        }

        Pair<Integer, Integer> costLocation = tierCostInfo.costLocation();
        DatabasePlayerPvE pveStats = databasePlayer.getPveStats();

        ItemMenuUtil.addSpendableCostRequirement(databasePlayer, menu, tierCostInfo.cost(), costLocation.getA(), costLocation.getB());
        ItemMenuUtil.addItemConfirmation(menu, () -> {
            addCraftItemConfirmation(player, databasePlayer, items, menu, requirements, pveStats, itemTier);
        });

        menu.setItem(4, 5, Menu.MENU_BACK, (m, e) -> openItemCraftingMenu(player, databasePlayer));
        menu.addBorder(Menu.GRAY_EMPTY_PANE, true);
        menu.openForPlayer(player);
    }

    public static void openItemSelectMenu(
            Player player,
            DatabasePlayer databasePlayer,
            @Nullable ItemTier tier,
            BiConsumer<Menu, InventoryClickEvent> back,
            TriConsumer<AbstractItem, Menu, InventoryClickEvent> onClick
    ) {
        ItemSearchMenu menu = new ItemSearchMenu(
                player,
                "Select an Item",
                onClick,
                itemBuilder -> itemBuilder.addLore(
                        Component.empty(),
                        Component.textOfChildren(
                                Component.text("CLICK", NamedTextColor.YELLOW, TextDecoration.BOLD),
                                Component.text(" to select", NamedTextColor.GREEN)
                        )
                ),
                new ItemSearchMenu.PlayerItemMenuSettings(databasePlayer)
                        .setItemInventory(databasePlayer.getPveStats()
                                                        .getItemsManager()
                                                        .getItemInventory()
                                                        .stream()
                                                        .filter(item -> tier == null || item.getTier() == tier)
                                                        .filter(item -> !(item instanceof AbstractFixedItem))
                                                        .collect(Collectors.toList())),
                databasePlayer,
                m -> {
                    m.setItem(4, 5, Menu.MENU_BACK, back);
                }
        );
        menu.open();
    }

    private static void addCraftItemConfirmation(
            Player player,
            DatabasePlayer databasePlayer,
            HashMap<ItemTier, AbstractItem> items,
            Menu menu,
            List<TierRequirement> requirements,
            DatabasePlayerPvE pveStats,
            ItemTier tier
    ) {
        boolean requirementsMet = requirements.stream().allMatch(requirement -> items.get(requirement.tier()) != null);
        boolean enoughMobDrops = TIER_COST_INFO.get(tier)
                                               .cost()
                                               .entrySet()
                                               .stream()
                                               .allMatch(entry -> entry.getKey().getFromPlayer(databasePlayer) >= entry.getValue());
        AbstractItem inheritedItem = null;
        if (tier == ItemTier.DELTA) {
            inheritedItem = items.get(ItemTier.GAMMA);
        } else if (tier == ItemTier.OMEGA) {
            inheritedItem = items.get(ItemTier.DELTA);
        }
        if (inheritedItem == null) {
            return;
        }

        Set<BasicStatPool> statPools = new HashSet<>(inheritedItem.getStatPool().keySet());
        BasicStatPool[] otherStats = Arrays.stream(BasicStatPool.VALUES)
                                           .filter(pool -> !statPools.contains(pool))
                                           .toArray(BasicStatPool[]::new);
        //add random other stat to stat pool
        BasicStatPool otherStat = otherStats[ThreadLocalRandom.current().nextInt(otherStats.length)];
        statPools.add(otherStat);

        AbstractItem itemToCraft;
        if (inheritedItem instanceof CraftsInto) {
            itemToCraft = ((CraftsInto) inheritedItem).getCraftsInto(statPools);
        } else {
            itemToCraft = null;
        }
        if (!(itemToCraft instanceof AbstractSpecialItem craftedItem)) {
            player.sendMessage(Component.text("An error occurred while crafting the item. Please report this!", NamedTextColor.RED));
            return;
        }
        ItemBuilder itemBuilder;
        if (requirementsMet && enoughMobDrops) {
            ItemStack craftedItemObfuscated = craftedItem.generateItemStackWithObfuscatedStat(otherStat);
            itemBuilder = new ItemBuilder(craftedItemObfuscated)
                    .name(Component.text("Click to Craft " + craftedItem.getName(), NamedTextColor.GREEN))
                    .addLore(Component.empty());
        } else {
            itemBuilder = new ItemBuilder(Material.BARRIER)
                    .name(Component.text("Click to Craft Item", NamedTextColor.GREEN))
                    .lore(
                            ItemMenuUtil.getRequirementMetString(requirementsMet, "Required Item" + (requirements.size() != 1 ? "s" : "") + " Selected"),
                            ItemMenuUtil.getRequirementMetString(enoughMobDrops, "Enough Mob Drops"),
                            Component.empty()
                    );
        }
        itemBuilder.addLore(
                WordWrap.wrap(Component.text("Crafted Item will inherit the type and stats pool of the highest tiered selected item. " +
                                "It will also have an additional random stat and its modifier will be randomized.", NamedTextColor.GRAY),
                        160
                )
        );
        menu.setItem(6, 2,
                itemBuilder.get(),
                (m, e) -> {
                    if (!requirementsMet) {
                        player.sendMessage(Component.text("You do not have all the required items to craft this item!", NamedTextColor.RED));
                        return;
                    }
                    TierCostInfo tierCostInfo = TIER_COST_INFO.get(tier);
                    for (Map.Entry<Spendable, Long> currenciesLongEntry : tierCostInfo.cost().entrySet()) {
                        Spendable spendable = currenciesLongEntry.getKey();
                        Long cost = currenciesLongEntry.getValue();
                        if (spendable.getFromPlayer(databasePlayer) < cost) {
                            player.sendMessage(Component.text("You need ", NamedTextColor.RED)
                                                        .append(spendable.getCostColoredName(cost))
                                                        .append(Component.text(" to craft this item!"))
                            );
                            return;
                        }
                    }

                    Menu.openConfirmationMenu(player,
                            "Confirm Item Craft",
                            3,
                            Collections.singletonList(Component.textOfChildren(
                                    Component.text("Craft ", NamedTextColor.GRAY),
                                    tier.getColoredName(),
                                    Component.text(" Item")
                            )),
                            Menu.GO_BACK,
                            (m2, e2) -> {
                                for (TierRequirement requirement : requirements) {
                                    pveStats.getItemsManager().removeItem(items.get(requirement.tier()));
                                }
                                for (Map.Entry<Spendable, Long> currenciesLongEntry : tierCostInfo.cost().entrySet()) {
                                    currenciesLongEntry.getKey().subtractFromPlayer(databasePlayer, currenciesLongEntry.getValue());
                                }
                                pveStats.getItemsManager().addItem(itemToCraft);
                                Bukkit.getServer().getPluginManager().callEvent(new ItemCraftEvent(player.getUniqueId(), itemToCraft));
                                AbstractItem.sendItemMessage(player,
                                        Component.textOfChildren(Component.text("You crafted ", NamedTextColor.GRAY), itemToCraft.getHoverComponent())
                                );
                                player.playSound(player.getLocation(), "mage.inferno.activation", 2, 0.5f);
                                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2, 1);
                                player.closeInventory();
                            },
                            (m2, e2) -> openForgingMenu(player, databasePlayer, tier, items),
                            (m2) -> {
                            }
                    );
                }
        );
    }

    private static void openCelestialSmelteryMenu(Player player, DatabasePlayer databasePlayer, Integer boughtBlessing) {
        Menu menu = new Menu("Celestial Smeltery", 9 * 6);

        ItemBuilder itemBuilder = new ItemBuilder(Material.PAPER)
                .name(Component.textOfChildren(
                        Component.text("CLICK", NamedTextColor.YELLOW, TextDecoration.BOLD),
                        Component.text(" to select a" + (boughtBlessing != null ? " different " : " ") + "blessing", NamedTextColor.GREEN)
                ))
                .enchant(Enchantment.OXYGEN, 1);
        if (boughtBlessing == null) {
            itemBuilder.name(Component.textOfChildren(
                    Component.text("CLICK", NamedTextColor.YELLOW, TextDecoration.BOLD),
                    Component.text(" to select a blessing", NamedTextColor.GREEN)
            ));
        } else {
            itemBuilder.name(Component.text("Tier " + (boughtBlessing) + " Bought Blessing", NamedTextColor.GREEN))
                       .addLore(
                               Component.empty(),
                               Component.textOfChildren(
                                       Component.text("CLICK", NamedTextColor.YELLOW, TextDecoration.BOLD),
                                       Component.text(" to select a different blessing", NamedTextColor.GREEN)
                               )
                       );
        }

        menu.setItem(1, 1,
                itemBuilder
                        .get(),
                (m, e) -> openBlessingSelectMenu(player, databasePlayer, boughtBlessing)
        );
        ItemMenuUtil.addPaneRequirement(menu, 2, 1, boughtBlessing != null);

        ItemMenuUtil.addSpendableCostRequirement(databasePlayer, menu, CELESTIAL_SMELTERY_COST, 1, 2);

        ItemMenuUtil.addItemConfirmation(menu, () -> {
            addCelestialSmelteryConfirmationMenu(player, databasePlayer, boughtBlessing, menu);
        });

        menu.setItem(4, 5, MENU_BACK, (m, e) -> openItemCraftingMenu(player, databasePlayer));
        menu.addBorder(Menu.GRAY_EMPTY_PANE, true);
        menu.openForPlayer(player);
    }

    private static void openBlessingSelectMenu(Player player, DatabasePlayer databasePlayer, Integer boughtBlessing) {
        Menu menu = new Menu("Select a Blessing Tier", 9 * 4);

        ItemsManager itemsManager = databasePlayer.getPveStats().getItemsManager();

        for (int tier = 1; tier <= 5; tier++) {
            Integer blessingBoughtAmount = itemsManager.getBlessingBoughtAmount(tier);
            int finalTier = tier;
            menu.setItem(tier + 1, 1,
                    new ItemBuilder(Material.PAPER)
                            .name(Component.text("Tier " + tier + " Bought Blessings", NamedTextColor.GREEN))
                            .lore(Component.textOfChildren(
                                    Component.text("Amount: ", NamedTextColor.GRAY),
                                    Component.text(blessingBoughtAmount, NamedTextColor.YELLOW)
                            ))
                            .enchant(Enchantment.OXYGEN, 1)
                            .get(),
                    (m, e) -> {
                        if (blessingBoughtAmount > 0) {
                            openCelestialSmelteryMenu(player, databasePlayer, finalTier);
                        }
                    }
            );
        }

        menu.setItem(4, 3, Menu.MENU_BACK, (m, e) -> openCelestialSmelteryMenu(player, databasePlayer, boughtBlessing));
        menu.openForPlayer(player);
    }

    private static void addCelestialSmelteryConfirmationMenu(Player player, DatabasePlayer databasePlayer, Integer boughtBlessing, Menu menu) {
        DatabasePlayerPvE pveStats = databasePlayer.getPveStats();

        boolean hasBoughtBlessing = boughtBlessing != null;
        boolean enoughCost = CELESTIAL_SMELTERY_COST.entrySet()
                                                    .stream()
                                                    .allMatch(entry -> entry.getKey().getFromPlayer(databasePlayer) >= entry.getValue());
        ItemBuilder itemBuilder = new ItemBuilder(hasBoughtBlessing && enoughCost ? Material.ANVIL : Material.BARRIER)
                .name(Component.text("Click to Smelt a Celestial Bronze", NamedTextColor.GREEN))
                .lore(
                        ItemMenuUtil.getRequirementMetString(hasBoughtBlessing, "Blessing Selected"),
                        ItemMenuUtil.getRequirementMetString(enoughCost, "Enough Loot")
                );

        menu.setItem(6, 2,
                itemBuilder.get(),
                (m, e) -> {
                    if (!hasBoughtBlessing || !enoughCost) {
                        return;
                    }

                    Menu.openConfirmationMenu(player,
                            "Confirm Smelt",
                            3,
                            new ArrayList<>() {{
                                add(Component.text("Smelt a Celestial Bronze", NamedTextColor.GRAY));
                                addAll(PvEUtils.getCostLore(CELESTIAL_SMELTERY_COST, "Smelt Cost", true));
                                add(Component.textOfChildren(
                                        Component.text(" - ", NamedTextColor.GRAY),
                                        Component.text("Tier " + boughtBlessing + " Bought Blessing", NamedTextColor.GREEN)
                                ));
                            }},
                            Menu.GO_BACK,
                            (m2, e2) -> {
                                for (Map.Entry<Spendable, Long> spendableLongEntry : CELESTIAL_SMELTERY_COST.entrySet()) {
                                    spendableLongEntry.getKey().subtractFromPlayer(databasePlayer, spendableLongEntry.getValue());
                                }
                                pveStats.getItemsManager().subtractBlessingBought(boughtBlessing);
                                pveStats.addCurrency(Currencies.CELESTIAL_BRONZE, 1);

                                DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                                player.playSound(player.getLocation(), "mage.inferno.activation", 2, 0.5f);
                                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2, 1);
                                player.closeInventory();

                                AbstractItem.sendItemMessage(player, Component.text("You smelted a Celestial Bronze", NamedTextColor.GREEN));
                            },
                            (m2, e2) -> openCelestialSmelteryMenu(player, databasePlayer, boughtBlessing),
                            (m2) -> {
                            }
                    );
                }
        );

    }

    record TierRequirement(ItemTier tier, int x, int y) {
    }

    record TierCostInfo(LinkedHashMap<Spendable, Long> cost, Pair<Integer, Integer> costLocation, List<TierRequirement> requirements) {

    }

}
