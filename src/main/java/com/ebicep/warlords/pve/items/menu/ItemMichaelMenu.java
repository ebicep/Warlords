package com.ebicep.warlords.pve.items.menu;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.items.pojos.WeeklyBlessings;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.PvEUtils;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.ItemsManager;
import com.ebicep.warlords.pve.items.menu.util.ItemMenuUtil;
import com.ebicep.warlords.pve.items.menu.util.ItemSearchMenu;
import com.ebicep.warlords.pve.items.modifiers.ItemModifier;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.mobs.MobDrops;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.java.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.*;
import java.util.stream.Collectors;

import static com.ebicep.warlords.menu.Menu.MENU_BACK;

public class ItemMichaelMenu {

    public static void openMichaelItemMenu(Player player, DatabasePlayer databasePlayer) {
        Menu menu = new Menu("Mysterious Michael", 9 * 4);

        menu.setItem(1, 1,
                new ItemBuilder(Material.BOOK)
                        .name(Component.text("Your Blessings", NamedTextColor.GREEN))
                        .lore(WordWrap.wrapWithNewline(Component.text("View your found and bought blessings", NamedTextColor.GRAY), 170))
                        .get(),
                (m, e) -> {
                    YourBlessingsMenu.openYourBlessingsMenu(player, databasePlayer);
                }
        );

        menu.setItem(3, 1,
                new ItemBuilder(Material.PAPER)
                        .name(Component.text("Buy a Blessing", NamedTextColor.GREEN))
                        .lore(WordWrap.wrap(Component.text("Buy blessings at the cost of mob drops.", NamedTextColor.GRAY), 170))
                        .addLore(Component.empty())
                        .addLoreC(WordWrap.wrap(Component.text(
                                        "There are 9 purchasable blessings per week. Higher tier blessings have a lower chance to be in stock.",
                                        NamedTextColor.GRAY
                                ), 150)
                        )
                        .get(),
                (m, e) -> {
                    BuyABlessingMenu.openBuyABlessingMenu(player, databasePlayer);
                }
        );
        menu.setItem(5, 1,
                new ItemBuilder(Material.ANVIL)
                        .name(Component.text("Apply a Blessing", NamedTextColor.GREEN))
                        .lore(WordWrap.wrap(Component.text("Items have different modified values which range from being:", NamedTextColor.GRAY), 170))
                        .addLore(
                                Component.textOfChildren(
                                        Component.text("  - ", NamedTextColor.GRAY),
                                        Component.text("Most Cursed (-5)", NamedTextColor.DARK_RED)
                                ),
                                Component.textOfChildren(
                                        Component.text("  - ", NamedTextColor.GRAY),
                                        Component.text("Normal (0)", NamedTextColor.WHITE)
                                ),
                                Component.textOfChildren(
                                        Component.text("  - ", NamedTextColor.GRAY),
                                        Component.text("Most Blessed (+5)", NamedTextColor.DARK_GREEN)
                                ),
                                Component.empty()
                        )
                        .addLoreC(
                                WordWrap.wrap(Component.text("Applying unknown blessings to an Item gives it a random blessing or curse, or does nothing. " +
                                                "The chance of a blessing or curse is based on the tier of the blessing.", NamedTextColor.GRAY),
                                        170
                                ))
                        .addLore(Component.empty())
                        .addLoreC(
                                WordWrap.wrap(Component.text(
                                        "Applying bought blessings to an Item has a guaranteed chance of blessing it, adding its tier to the current modified value.",
                                        NamedTextColor.GRAY
                                ), 170)
                        )
                        .get(),
                (m, e) -> {
                    ApplyBlessingMenu.openApplyBlessingMenu(player, databasePlayer, new ApplyBlessingMenu.ApplyBlessingMenuData());

                }
        );
        menu.setItem(7, 1,
                new ItemBuilder(Material.MILK_BUCKET)
                        .name(Component.text("Remove a Curse", NamedTextColor.GREEN))
                        .lore(WordWrap.wrapWithNewline(Component.text("Removing a Curse on an Item will lower its curse effectiveness by a tier.",
                                NamedTextColor.GRAY
                        ), 150))
                        .get(),
                (m, e) -> {
                    RemoveACurseMenu.openPurifyItemMenu(player, databasePlayer, null);
                }
        );

        menu.setItem(4, 3, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static class YourBlessingsMenu {

        public static void openYourBlessingsMenu(Player player, DatabasePlayer databasePlayer) {
            Menu menu = new Menu("Your Blessings", 9 * 4);

            ItemsManager itemsManager = databasePlayer.getPveStats().getItemsManager();
            menu.setItem(1, 1,
                    new ItemBuilder(Material.BOOK)
                            .name(Component.text("Unknown Blessings", NamedTextColor.GREEN))
                            .lore(WordWrap.wrap(Component.text("You can find unknown blessings by killing mobs.", NamedTextColor.GRAY), 150))
                            .addLore(
                                    Component.empty(),
                                    Component.text("Bought Blessings", NamedTextColor.GREEN)
                            )
                            .addLoreC(
                                    WordWrap.wrap(Component.text("You can buy blessings through Michael. " +
                                            "Bought blessings have a guaranteed chance of applying its tier.", NamedTextColor.GRAY), 150)
                            )
                            .get(),
                    (m, e) -> {
                    }
            );
            int blessingsFound = itemsManager.getBlessingsFound();
            menu.setItem(2, 1,
                    new ItemBuilder(Material.PAPER)
                            .name(Component.text("Unknown Blessings", NamedTextColor.GREEN))
                            .lore(Component.textOfChildren(
                                    Component.text("Amount: ", NamedTextColor.GRAY),
                                    Component.text(blessingsFound, NamedTextColor.YELLOW)
                            ))
                            .amount(blessingsFound)
                            .get(),
                    (m, e) -> {

                    }
            );
            for (int tier = 1; tier <= 5; tier++) {
                Integer blessingBoughtAmount = itemsManager.getBlessingBoughtAmount(tier);
                menu.setItem(tier + 2, 1,
                        new ItemBuilder(Material.PAPER)
                                .name(Component.text("Tier " + tier + " Bought Blessings", NamedTextColor.GREEN))
                                .lore(Component.textOfChildren(
                                        Component.text("Amount: ", NamedTextColor.GRAY),
                                        Component.text(blessingBoughtAmount, NamedTextColor.YELLOW)
                                ))
                                .amount(blessingBoughtAmount)
                                .enchant(Enchantment.OXYGEN, 1)
                                .flags(ItemFlag.HIDE_ENCHANTS)
                                .get(),
                        (m, e) -> {

                        }
                );
            }

            menu.setItem(4, 3, MENU_BACK, (m, e) -> openMichaelItemMenu(player, databasePlayer));
            menu.addBorder(Menu.GRAY_EMPTY_PANE, true);
            menu.openForPlayer(player);
        }

    }

    public static class BuyABlessingMenu {

        private static final HashMap<Integer, LinkedHashMap<Spendable, Long>> COSTS = new HashMap<>() {{
            put(1, new LinkedHashMap<>() {{

                put(Currencies.COIN, 500_000L);
            }});
            put(2, new LinkedHashMap<>() {{

                put(Currencies.SYNTHETIC_SHARD, 15_000L);
            }});
            put(3, new LinkedHashMap<>() {{

                put(Currencies.LEGEND_FRAGMENTS, 2_500L);
            }});
            put(4, new LinkedHashMap<>() {{

                put(Currencies.LEGEND_FRAGMENTS, 5_000L);
                put(MobDrops.ZENITH_STAR, 5L);
            }});
            put(5, new LinkedHashMap<>() {{

                put(MobDrops.ZENITH_STAR, 10L);
            }});
        }};
        private static final HashMap<Integer, List<Component>> COSTS_LORE = new HashMap<>() {{
            COSTS.forEach((tier, costs) -> {
                put(tier, PvEUtils.getCostLore(costs, true));
            });
        }};

        public static void openBuyABlessingMenu(Player player, DatabasePlayer databasePlayer) {
            WeeklyBlessings currentWeeklyBlessings = WeeklyBlessings.currentWeeklyBlessings;
            if (currentWeeklyBlessings == null) {
                player.sendMessage(Component.text("There are no weekly blessings available at this time.", NamedTextColor.RED));
                return;
            }
            DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
            Map<Integer, Integer> playerOrder = currentWeeklyBlessings.getPlayerOrders().getOrDefault(player.getUniqueId(), new HashMap<>());

            Menu menu = new Menu("Buy a Blessing", 9 * 4);
            for (int tier = 1; tier <= 5; tier++) {
                int stock = currentWeeklyBlessings.getStock().getOrDefault(tier, 0) - playerOrder.getOrDefault(tier, 0);
                int finalTier = tier;
                List<Component> lore = COSTS_LORE.get(tier);
                menu.setItem(tier + 1, 1,
                        new ItemBuilder(Material.PAPER)
                                .name(Component.text("Tier " + tier, NamedTextColor.GREEN))
                                .lore(Component.textOfChildren(
                                        Component.text("Stock: ", NamedTextColor.GRAY)
                                                 .append(Component.text(stock, NamedTextColor.YELLOW))
                                ))
                                .addLoreC(lore)
                                .amount(tier)
                                .enchant(Enchantment.OXYGEN, 1)
                                .flags(ItemFlag.HIDE_ENCHANTS)
                                .get(),
                        (m, e) -> {
                            if (stock <= 0) {
                                player.sendMessage(Component.text("This blessing is out of stock!", NamedTextColor.RED));
                                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 2, 0.5f);
                                return;
                            }
                            LinkedHashMap<Spendable, Long> tierCosts = COSTS.get(finalTier);
                            for (Map.Entry<Spendable, Long> spendableIntegerEntry : tierCosts.entrySet()) {
                                Spendable spendable = spendableIntegerEntry.getKey();
                                Long cost = spendableIntegerEntry.getValue();
                                if (spendable.getFromPlayer(databasePlayer) < cost) {
                                    player.sendMessage(Component.text("You need ", NamedTextColor.RED)
                                                                .append(spendable.getCostColoredName(cost))
                                                                .append(Component.text(" to bless this item!"))
                                    );
                                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 2, 0.5f);
                                    return;
                                }
                            }
                            Menu.openConfirmationMenu0(
                                    player,
                                    "Buy Blessing",
                                    3,
                                    new ArrayList<>(lore) {{
                                        add(0, Component.text("Buy ", NamedTextColor.GRAY)
                                                        .append(Component.text("Tier " + finalTier, NamedTextColor.GREEN))
                                                        .append(Component.text(" Blessing")));
                                    }},
                                    Menu.GO_BACK,
                                    (m2, e2) -> {
                                        currentWeeklyBlessings.addPlayerOrder(player.getUniqueId(), finalTier);
                                        tierCosts.forEach((spendable, cost) -> spendable.subtractFromPlayer(databasePlayer, cost));
                                        pveStats.getItemsManager().addBlessingBought(finalTier);
                                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2, 1.5f);
                                        player.closeInventory();

                                        AbstractItem.sendItemMessage(player, Component.text("You bought a ", NamedTextColor.GRAY)
                                                                                      .append(Component.text("Tier " + finalTier, NamedTextColor.GREEN))
                                                                                      .append(Component.text(" Blessing!"))
                                        );
                                    },
                                    (m2, e2) -> openBuyABlessingMenu(player, databasePlayer),
                                    (m2) -> {
                                    }
                            );
                        }
                );
            }

            menu.setItem(4, 3, MENU_BACK, (m, e) -> openMichaelItemMenu(player, databasePlayer));
            menu.addBorder(Menu.GRAY_EMPTY_PANE, true);
            menu.openForPlayer(player);
        }

    }

    public static class ApplyBlessingMenu {

        private static final LinkedHashMap<Spendable, Long> FOUND_COST = new LinkedHashMap<>() {{
            put(Currencies.COIN, 50_000L);
        }};

        public static void openApplyBlessingMenu(Player player, DatabasePlayer databasePlayer, ApplyBlessingMenuData menuData) {
            AbstractItem item = menuData.getItem();
            Integer blessing = menuData.getBlessing();
            ItemBuilder selectedBlessing;
            if (item != null) {
                if (blessing != null) {
                    boolean blessingFound = menuData.isBlessingFound();
                    selectedBlessing = new ItemBuilder(Material.PAPER)
                            .name(Component.text("Tier " + (blessing + 1) + (blessingFound ? " Found" : " Bought") + " Blessing", NamedTextColor.GREEN))
                            .addLoreC(blessingFound ? menuData.getBlessingCurseFoundLore() : menuData.getBlessingCurseBoughtLore())
                            .addLore(
                                    Component.empty(),
                                    Component.textOfChildren(
                                            Component.text("CLICK", NamedTextColor.YELLOW, TextDecoration.BOLD),
                                            Component.text(" to select a different blessing", NamedTextColor.GREEN)
                                    )
                            );
                    if (!blessingFound) {
                        selectedBlessing.enchant(Enchantment.OXYGEN, 1);
                        selectedBlessing.flags(ItemFlag.HIDE_ENCHANTS);
                    }
                } else {
                    selectedBlessing = new ItemBuilder(Material.PAPER)
                            .name(Component.textOfChildren(
                                            Component.text("CLICK", NamedTextColor.YELLOW, TextDecoration.BOLD),
                                            Component.text(" to select a blessing", NamedTextColor.GREEN)
                                    )
                            );
                }
            } else {
                selectedBlessing = new ItemBuilder(Material.MAP)
                        .name(Component.text("Select an Item first", NamedTextColor.RED));
            }


            Menu menu = new Menu("Apply a Blessing", 9 * 6);
            ItemMenuUtil.addItemTierRequirement(
                    menu,
                    ItemTier.NONE,
                    item,
                    1,
                    1,
                    (m, e) ->
                            openItemSelectMenu(
                                    player,
                                    databasePlayer,
                                    new ItemSearchMenu.PlayerItemMenuSettings(databasePlayer),
                                    menuData
                            )
            );
            menu.setItem(1, 2,
                    selectedBlessing.get(),
                    (m, e) -> {
                        if (item == null) {
                            player.sendMessage(Component.text("Select an Item first!", NamedTextColor.RED));
                            return;
                        }
                        openBlessingSelectMenu(player, databasePlayer, menuData);
                    }
            );
            ItemMenuUtil.addPaneRequirement(menu, 2, 2, blessing != null);

            if (blessing != null && menuData.isBlessingFound()) {
                ItemMenuUtil.addSpendableCostRequirement(databasePlayer, menu, FOUND_COST, 1, 3);
            }

            ItemMenuUtil.addItemConfirmation(menu, () -> {
                addCraftItemConfirmation(player, databasePlayer, menuData, menu);
            });

            menu.setItem(4, 5, MENU_BACK, (m, e) -> openMichaelItemMenu(player, databasePlayer));
            menu.addBorder(Menu.GRAY_EMPTY_PANE, true);
            menu.openForPlayer(player);
        }

        private static void openItemSelectMenu(
                Player player,
                DatabasePlayer databasePlayer,
                ItemSearchMenu.PlayerItemMenuSettings menuSettings,
                ApplyBlessingMenuData menuData
        ) {
            ItemSearchMenu menu = new ItemSearchMenu(
                    player,
                    "Select an Item",
                    (newItem, m, e) -> {
                        AbstractItem previousItem = menuData.getItem();
                        //prevent non-normal item from being blessed with bought blessing
                        if (previousItem != null && previousItem.getModifier() == 0 && newItem.getModifier() != 0 && !menuData.isBlessingFound()) {
                            menuData.setBlessing(null);
                        }
                        menuData.setItem(newItem);
                        openApplyBlessingMenu(player, databasePlayer, menuData);
                    },
                    itemBuilder -> itemBuilder.addLore(
                            Component.empty(),
                            Component.textOfChildren(
                                    Component.text("CLICK", NamedTextColor.YELLOW, TextDecoration.BOLD),
                                    Component.text(" to select", NamedTextColor.GREEN)
                            )
                    ),
                    menuSettings,
                    databasePlayer,
                    m -> m.setItem(4, 5,
                            Menu.MENU_BACK,
                            (m2, e2) -> {
                                openApplyBlessingMenu(player, databasePlayer, menuData);
                            }
                    )
            );

            menu.open();
        }

        private static void openBlessingSelectMenu(
                Player player,
                DatabasePlayer databasePlayer,
                ApplyBlessingMenuData menuData
        ) {
            Menu menu = new Menu("Select a Blessing Tier", 9 * 4);
            ItemsManager itemsManager = databasePlayer.getPveStats().getItemsManager();
            menu.setItem(1, 1,
                    new ItemBuilder(Material.BOOK)
                            .name(Component.text("Unknown Blessings", NamedTextColor.GREEN))
                            .lore(WordWrap.wrap(Component.text("You can find unknown blessings by killing mobs.", NamedTextColor.GRAY), 150))
                            .addLore(
                                    Component.empty(),
                                    Component.text("Bought Blessings", NamedTextColor.GREEN)
                            )
                            .addLoreC(
                                    WordWrap.wrap(Component.text("You can buy blessings through Michael. " +
                                            "Bought blessings have a guaranteed chance of applying its tier.", NamedTextColor.GRAY), 150)
                            )
                            .get(),
                    (m, e) -> {
                    }
            );
            int blessingsFound = itemsManager.getBlessingsFound();
            menu.setItem(2, 1,
                    new ItemBuilder(Material.PAPER)
                            .name(Component.text("Unknown Blessings", NamedTextColor.GREEN))
                            .lore(Component.textOfChildren(
                                            Component.text("Amount: ", NamedTextColor.GRAY)
                                                     .append(Component.text(blessingsFound, NamedTextColor.YELLOW))),
                                    Component.empty(),
                                    Component.textOfChildren(
                                            Component.text("Bless Chance: ", NamedTextColor.GRAY),
                                            Component.text(NumberFormat.formatOptionalTenths(menuData.getItem().getTier().blessedChance * 100) + "%",
                                                    NamedTextColor.YELLOW
                                            )
                                    ),
                                    Component.textOfChildren(
                                            Component.text("Curse Chance: ", NamedTextColor.GRAY),
                                            Component.text(NumberFormat.formatOptionalTenths(menuData.getItem().getTier().cursedChance * 100) + "%",
                                                    NamedTextColor.YELLOW
                                            )
                                    ),
                                    Component.empty(),
                                    blessingsFound > 0 ?
                                    Component.textOfChildren(
                                            Component.text("CLICK", NamedTextColor.YELLOW, TextDecoration.BOLD),
                                            Component.text(" to select", NamedTextColor.GREEN)
                                    ) :
                                    Component.text("You have no unknown blessings", NamedTextColor.RED)
                            )
                            .amount(blessingsFound)
                            .get(),
                    (m, e) -> {
                        if (blessingsFound <= 0) {
                            player.sendMessage(Component.text("You have no unknown blessings!", NamedTextColor.RED));
                            return;
                        }
                        menuData.setBlessing(0);
                        menuData.setBlessingFound(true);
                        openApplyBlessingMenu(player, databasePlayer, menuData);
                    }
            );
            for (int tier = 1; tier <= 5; tier++) {
                int finalTier = tier;
                Integer blessingBoughtAmount = itemsManager.getBlessingBoughtAmount(tier);
                boolean normalItem = menuData.getItem().getModifier() == 0;
                ItemBuilder itemBuilder = new ItemBuilder(normalItem ? Material.PAPER : Material.BARRIER)
                        .name(Component.text("Tier " + tier, NamedTextColor.GREEN))
                        .lore(
                                Component.textOfChildren(
                                        Component.text("Amount: ", NamedTextColor.GRAY),
                                        Component.text(blessingBoughtAmount, NamedTextColor.YELLOW)
                                ),
                                Component.empty(),
                                Component.textOfChildren(
                                        Component.text("Bless Chance: ", NamedTextColor.GRAY),
                                        Component.text("100%", NamedTextColor.YELLOW)
                                ),
                                Component.textOfChildren(
                                        Component.text("Curse Chance: ", NamedTextColor.GRAY),
                                        Component.text("0%", NamedTextColor.YELLOW)
                                ),
                                Component.empty(),
                                normalItem ? blessingBoughtAmount > 0 ?
                                             Component.textOfChildren(
                                                     Component.text("CLICK", NamedTextColor.YELLOW, TextDecoration.BOLD),
                                                     Component.text(" to select", NamedTextColor.GREEN)
                                             ) :
                                             Component.text("You have no blessings of this tier", NamedTextColor.RED) :
                                Component.text("Only applicable to non blessed/cursed items", NamedTextColor.RED)
                        )
                        .amount(blessingBoughtAmount);
                if (normalItem) {
                    itemBuilder.enchant(Enchantment.OXYGEN, 1);
                    itemBuilder.flags(ItemFlag.HIDE_ENCHANTS);
                }
                menu.setItem(tier + 2, 1,
                        itemBuilder.get(),
                        (m, e) -> {
                            if (!normalItem) {
                                player.sendMessage(Component.text("Only applicable to non blessed/cursed items", NamedTextColor.RED));
                                return;
                            }
                            if (blessingBoughtAmount <= 0) {
                                player.sendMessage(Component.text("You have no blessings of this tier!", NamedTextColor.RED));
                                return;
                            }
                            menuData.setBlessing(finalTier - 1);
                            menuData.setBlessingFound(false);
                            openApplyBlessingMenu(player, databasePlayer, menuData);
                        }
                );
            }
            menu.setItem(4, 3,
                    Menu.MENU_BACK,
                    (m, e) -> {
                        openApplyBlessingMenu(player, databasePlayer, menuData);
                    }
            );
            menu.openForPlayer(player);
        }

        private static void addCraftItemConfirmation(
                Player player,
                DatabasePlayer databasePlayer,
                ApplyBlessingMenuData menuData,
                Menu menu
        ) {
            DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
            AbstractItem item = menuData.getItem();
            Integer blessing = menuData.getBlessing();
            boolean blessingFound = menuData.isBlessingFound();
            boolean enoughCost = blessingFound && FOUND_COST.entrySet()
                                                            .stream()
                                                            .allMatch(entry -> entry.getKey().getFromPlayer(databasePlayer) >= entry.getValue());
            ItemBuilder itemBuilder = new ItemBuilder((item != null && blessing != null && (!blessingFound || enoughCost) ? Material.ANVIL : Material.BARRIER))
                    .name(Component.text("Click to Apply Blessing", NamedTextColor.GREEN))
                    .lore(
                            ItemMenuUtil.getRequirementMetString(item != null, "Item Selected"),
                            ItemMenuUtil.getRequirementMetString(blessing != null, "Blessing Selected")
                    );
            if (blessing != null && blessingFound) {
                itemBuilder.addLore(ItemMenuUtil.getRequirementMetString(enoughCost, "Enough Coins"));
            }
            menu.setItem(6, 2,
                    itemBuilder.get(),
                    (m, e) -> {
                        if (item == null || blessing == null || (blessingFound && !enoughCost)) {
                            return;
                        }

                        Menu.openConfirmationMenu0(player,
                                "Confirm Item Blessing",
                                3,
                                new ArrayList<>() {{
                                    add(Component.text("Apply Blessing", NamedTextColor.GRAY));
                                    addAll(blessingFound ? menuData.getBlessingCurseFoundLore() : menuData.getBlessingCurseBoughtLore());
                                }},
                                Menu.GO_BACK,
                                (m2, e2) -> {
                                    int tier = blessing + 1;
                                    Component component = Component.text("You applied a" + (blessingFound ? "n unknown" : " bought tier " + tier) + " blessing on ",
                                                                           NamedTextColor.GRAY
                                                                   )
                                                                   .append(item.getHoverComponent())
                                                                   .append(Component.text(" and it became "));

                                    if (blessingFound) {
                                        pveStats.getItemsManager().subtractBlessingsFound(1);
                                        item.bless(null);
                                        for (Map.Entry<Spendable, Long> currenciesLongEntry : FOUND_COST.entrySet()) {
                                            currenciesLongEntry.getKey().subtractFromPlayer(databasePlayer, currenciesLongEntry.getValue());
                                        }
                                    } else {
                                        pveStats.getItemsManager().subtractBlessingBought(tier);
                                        item.setModifier(tier);
                                    }
                                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2, 2);
                                    player.closeInventory();

                                    AbstractItem.sendItemMessage(player, component.hoverEvent(item.getHoverComponent()));
                                },
                                (m2, e2) -> openApplyBlessingMenu(player, databasePlayer, menuData),
                                (m2) -> {
                                }
                        );
                    }
            );
        }

        private static class ApplyBlessingMenuData {
            private AbstractItem item;
            private Integer blessing;
            private boolean blessingFound;

            public ApplyBlessingMenuData() {
            }

            public List<Component> getBlessingCurseFoundLore() {
                return new ArrayList<>() {{
                    add(Component.empty());
                    add(Component.textOfChildren(
                            Component.text("Dud Chance", NamedTextColor.WHITE),
                            Component.text(" - ", NamedTextColor.GRAY),
                            Component.text(NumberFormat.formatOptionalHundredths((1 - item.getTier().blessedChance - item.getTier().cursedChance) * 100) + "%",
                                    NamedTextColor.YELLOW
                            )
                    ));
                    add(Component.empty());
                    add(Component.textOfChildren(
                            Component.text("Bless Chance", NamedTextColor.DARK_GREEN),
                            Component.text(" - ", NamedTextColor.GRAY),
                            Component.text(NumberFormat.formatOptionalHundredths(item.getTier().blessedChance * 100) + "%", NamedTextColor.YELLOW)
                    ));
                    for (int i = 1; i <= 5; i++) {
                        add(Component.text("   ").append(getModifiedLore(i, ItemModifier.BLESSING_TIER_CHANCE.get(i), 5)));
                    }
                    add(Component.empty());
                    add(Component.textOfChildren(
                            Component.text("Curse Chance", NamedTextColor.DARK_RED),
                            Component.text(" - ", NamedTextColor.GRAY),
                            Component.text(NumberFormat.formatOptionalHundredths(item.getTier().cursedChance * 100) + "%", NamedTextColor.YELLOW)
                    ));
                    for (int i = 1; i <= 5; i++) {
                        add(Component.text("   ").append(getModifiedLore(-i, ItemModifier.CURSE_TIER_CHANCE.get(i), 5)));
                    }
                }};
            }

            private Component getModifiedLore(int newModifier, double chance, int extraSpace) {
                if (newModifier == 0) {
                    return Component.textOfChildren(
                            Component.text("Normal", NamedTextColor.WHITE),
                            Component.text(" - ", NamedTextColor.GRAY),
                            Component.text(NumberFormat.formatOptionalHundredths(item.getTier().blessedChance * 100) + "%", NamedTextColor.YELLOW)
                    );
                } else {
                    ItemModifier itemModifier = item.getItemModifier(newModifier);
                    boolean isBlessing = newModifier > 0;
                    return Component.textOfChildren(
                            Component.text(itemModifier.getName(), isBlessing ? NamedTextColor.GREEN : NamedTextColor.RED),
                            Component.text(" - ", NamedTextColor.GRAY),
                            Component.text((blessingFound ? NumberFormat.formatOptionalHundredths(chance) : (isBlessing ? "100" : "0")) + "%",
                                    NamedTextColor.YELLOW
                            ),
                            Component.newline(),
                            Component.text(" ".repeat(extraSpace + 1) + itemModifier.getDescription(), NamedTextColor.GREEN)
                    );
                }
            }

            public List<Component> getBlessingCurseBoughtLore() {
                if (!blessingFound) {
                    ItemModifier itemBlessing = item.getBlessings()[blessing];
                    return Arrays.asList(
                            Component.empty(),
                            Component.textOfChildren(
                                    Component.text(itemBlessing.getName(), NamedTextColor.GREEN),
                                    Component.text(" - ", NamedTextColor.GRAY),
                                    Component.text((blessingFound ? NumberFormat.formatOptionalHundredths(item.getTier().blessedChance * 100) : "100") + "%",
                                            NamedTextColor.YELLOW
                                    ),
                                    Component.text(" " + itemBlessing.getDescription(), NamedTextColor.GREEN)
                            )
                    );
                }
                int modifier = item.getModifier();
                int tier = blessing + 1;
                return Arrays.asList(
                        Component.empty(),
                        getModifiedLore(Math.min(modifier + tier, 5), item.getTier().blessedChance * 100, 0),
                        Component.empty(),
                        getModifiedLore(Math.max(modifier - tier, -5), item.getTier().cursedChance * 100, 0)
                );
            }

            public AbstractItem getItem() {
                return item;
            }

            public void setItem(AbstractItem item) {
                this.item = item;
            }

            public Integer getBlessing() {
                return blessing;
            }

            public void setBlessing(Integer blessing) {
                this.blessing = blessing;
            }

            public boolean isBlessingFound() {
                return blessingFound;
            }

            public void setBlessingFound(boolean blessingFound) {
                this.blessingFound = blessingFound;
            }
        }
    }

    public static class RemoveACurseMenu {

        public static void openPurifyItemMenu(Player player, DatabasePlayer databasePlayer, AbstractItem item) {
            Menu menu = new Menu("Remove a Curse", 9 * 6);
            ItemMenuUtil.addItemTierRequirement(
                    menu,
                    ItemTier.NONE,
                    item,
                    1,
                    1,
                    (m, e) ->
                            openItemSelectMenu(
                                    player,
                                    databasePlayer,
                                    new ItemSearchMenu.PlayerItemMenuSettings(databasePlayer)
                                            .setItemInventory(databasePlayer.getPveStats()
                                                                            .getItemsManager()
                                                                            .getItemInventory()
                                                                            .stream()
                                                                            .filter(i -> i.getModifier() < 0)
                                                                            .collect(Collectors.toList())),
                                    item
                            )
            );
            if (item != null) {
                ItemMenuUtil.addSpendableCostRequirement(databasePlayer, menu, item.getTier().removeCurseCost, 1, 2);
            }
            ItemMenuUtil.addItemConfirmation(menu, () -> {
                addPurifyItemConfirmation(player, databasePlayer, item, menu);
            });

            menu.setItem(4, 5, MENU_BACK, (m, e) -> openMichaelItemMenu(player, databasePlayer));
            menu.addBorder(Menu.GRAY_EMPTY_PANE, true);
            menu.openForPlayer(player);
        }

        private static void openItemSelectMenu(
                Player player,
                DatabasePlayer databasePlayer,
                ItemSearchMenu.PlayerItemMenuSettings menuSettings,
                AbstractItem item
        ) {
            ItemSearchMenu menu = new ItemSearchMenu(
                    player, "Select an Item",
                    (i, m, e) -> {
                        openPurifyItemMenu(player, databasePlayer, i);
                    },
                    itemBuilder -> itemBuilder.addLore(
                            Component.empty(),
                            Component.textOfChildren(
                                    Component.text("CLICK", NamedTextColor.YELLOW, TextDecoration.BOLD),
                                    Component.text(" to select", NamedTextColor.GREEN)
                            )
                    ),
                    menuSettings,
                    databasePlayer,
                    m -> m.setItem(4, 5,
                            Menu.MENU_BACK,
                            (m2, e2) -> {
                                openPurifyItemMenu(player, databasePlayer, item);
                            }
                    )
            );

            menu.open();
        }

        private static void addPurifyItemConfirmation(
                Player player,
                DatabasePlayer databasePlayer,
                AbstractItem item,
                Menu menu
        ) {
            boolean enoughCost = item != null &&
                    item.getTier().removeCurseCost.entrySet()
                                                  .stream()
                                                  .allMatch(entry -> entry.getKey().getFromPlayer(databasePlayer) >= entry.getValue());
            menu.setItem(6, 2,
                    new ItemBuilder(item != null && enoughCost ? Material.MILK_BUCKET : Material.BARRIER)
                            .name(Component.text("Click to Purify Item", NamedTextColor.GREEN))
                            .lore(
                                    ItemMenuUtil.getRequirementMetString(item != null, "Item Selected"),
                                    ItemMenuUtil.getRequirementMetString(enoughCost, "Enough Loot")
                            )
                            .get(),
                    (m, e) -> {
                        if (item == null) {
                            player.sendMessage(Component.text("Select an Item first!", NamedTextColor.RED));
                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 2, 0.5f);
                            return;
                        }
                        LinkedHashMap<Spendable, Long> removeCurseCost = item.getTier().removeCurseCost;
                        for (Map.Entry<Spendable, Long> spendableLongEntry : removeCurseCost.entrySet()) {
                            Spendable spendable = spendableLongEntry.getKey();
                            Long cost = spendableLongEntry.getValue();
                            if (spendable.getFromPlayer(databasePlayer) < cost) {
                                player.sendMessage(Component.text("You need ", NamedTextColor.RED)
                                                            .append(spendable.getCostColoredName(cost))
                                                            .append(Component.text(" to purify this Item!"))
                                );
                                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 2, 0.5f);
                                return;
                            }
                        }


                        Menu.openConfirmationMenu0(player,
                                "Confirm Purification",
                                3,
                                new ArrayList<>() {{
                                    if (item.getModifier() == -1) {
                                        add(item.getCurses()[-item.getModifier() - 1].getDescription().decorate(TextDecoration.STRIKETHROUGH));
                                    } else {
                                        add(Component.textOfChildren(
                                                item.getCurses()[-item.getModifier() - 1].getDescription(),
                                                Component.text(" > ", NamedTextColor.DARK_GREEN),
                                                item.getCurses()[-item.getModifier() - 2].getDescription()
                                        ));
                                    }
                                    addAll(PvEUtils.getCostLore(removeCurseCost, true));
                                }},
                                Menu.GO_BACK,
                                (m2, e2) -> {
                                    Component component = Component.text("You decreased the tier of curse from ", NamedTextColor.GRAY)
                                                                   .append(item.getHoverComponent())
                                                                   .append(Component.text(" and it became "));

                                    for (Map.Entry<Spendable, Long> spendableLongEntry : removeCurseCost.entrySet()) {
                                        spendableLongEntry.getKey().subtractFromPlayer(databasePlayer, spendableLongEntry.getValue());
                                    }
                                    item.setModifier(item.getModifier() + 1);
                                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                                    player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_SPLASH, 2, 0.1f);
                                    player.closeInventory();

                                    AbstractItem.sendItemMessage(player, component.append(item.getHoverComponent()));
                                },
                                (m2, e2) -> openPurifyItemMenu(player, databasePlayer, item),
                                (m2) -> {
                                }
                        );
                    }
            );
        }
    }
}
