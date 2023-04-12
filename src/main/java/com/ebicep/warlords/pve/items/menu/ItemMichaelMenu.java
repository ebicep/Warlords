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
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.NumberFormat;
import org.bukkit.ChatColor;
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
                        .name(ChatColor.GREEN + "Your Blessings")
                        .lore(WordWrap.wrapWithNewline(ChatColor.GRAY + "View your found and bought blessings", 170))
                        .get(),
                (m, e) -> {
                    YourBlessingsMenu.openYourBlessingsMenu(player, databasePlayer);
                }
        );

        menu.setItem(3, 1,
                new ItemBuilder(Material.PAPER)
                        .name(ChatColor.GREEN + "Buy a Blessing")
                        .lore(
                                WordWrap.wrapWithNewline(ChatColor.GRAY + "Buy blessings at the cost of mob drops.", 170),
                                "",
                                WordWrap.wrapWithNewline(
                                        ChatColor.GRAY + "There are 9 purchasable blessings per week. Higher tier blessings have a lower chance to be in stock.",
                                        150
                                )
                        )
                        .get(),
                (m, e) -> {
                    BuyABlessingMenu.openBuyABlessingMenu(player, databasePlayer);
                }
        );
        menu.setItem(5, 1,
                new ItemBuilder(Material.ANVIL)
                        .name(ChatColor.GREEN + "Apply a Blessing")
                        .lore(
                                WordWrap.wrapWithNewline(ChatColor.GRAY + "Items have different modified values which range from being:", 170),
                                ChatColor.GRAY + "  - " + ChatColor.DARK_RED + "Most Cursed (-5)",
                                ChatColor.GRAY + "  - " + ChatColor.WHITE + "Normal (0)",
                                ChatColor.GRAY + "  - " + ChatColor.DARK_GREEN + "Most Blessed (+5)",
                                "",
                                WordWrap.wrapWithNewline(ChatColor.GRAY + "Applying unknown blessings to an Item gives it a random blessing or curse, or does nothing. " +
                                                "The chance of a blessing or curse is based on the tier of the blessing.",
                                        170
                                ),
                                "",
                                WordWrap.wrapWithNewline(ChatColor.GRAY + "Applying bought blessings to an Item has a guaranteed chance of blessing it, adding its tier to the current modified value.",
                                        170
                                )
                        )
                        .get(),
                (m, e) -> {
                    ApplyBlessingMenu.openApplyBlessingMenu(player, databasePlayer, new ApplyBlessingMenu.ApplyBlessingMenuData());

                }
        );
        menu.setItem(7, 1,
                new ItemBuilder(Material.MILK_BUCKET)
                        .name(ChatColor.GREEN + "Remove a Curse")
                        .lore(WordWrap.wrapWithNewline(ChatColor.GRAY + "Removing a Curse on an Item will lower its curse effectiveness by a tier.", 150))
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
                            .name(ChatColor.GREEN + "Unknown Blessings")
                            .lore(
                                    WordWrap.wrapWithNewline(ChatColor.GRAY + "You can find unknown blessings by killing mobs.", 150),
                                    "",
                                    ChatColor.GREEN + "Bought Blessings",
                                    WordWrap.wrapWithNewline(ChatColor.GRAY + "You can buy blessings through Michael. " +
                                            "Bought blessings have a guaranteed chance of applying its tier.", 150)
                            )
                            .get(),
                    (m, e) -> {
                    }
            );
            int blessingsFound = itemsManager.getBlessingsFound();
            menu.setItem(2, 1,
                    new ItemBuilder(Material.PAPER)
                            .name(ChatColor.GREEN + "Unknown Blessings")
                            .lore(ChatColor.GRAY + "Amount: " + ChatColor.YELLOW + blessingsFound)
                            .amount(blessingsFound)
                            .get(),
                    (m, e) -> {

                    }
            );
            for (int tier = 1; tier <= 5; tier++) {
                Integer blessingBoughtAmount = itemsManager.getBlessingBoughtAmount(tier);
                menu.setItem(tier + 2, 1,
                        new ItemBuilder(Material.PAPER)
                                .name(ChatColor.GREEN + "Tier " + tier + " Bought Blessings")
                                .lore(ChatColor.GRAY + "Amount: " + ChatColor.YELLOW + blessingBoughtAmount)
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
        private static final HashMap<Integer, List<String>> COSTS_LORE = new HashMap<>() {{
            COSTS.forEach((tier, costs) -> {
                put(tier, new ArrayList<>() {{
                    add("");
                    add(ChatColor.AQUA + "Cost: ");
                    costs.forEach((currency, amount) -> add(ChatColor.GRAY + " - " + currency.getCostColoredName(amount)));
                }});
            });
        }};

        public static void openBuyABlessingMenu(Player player, DatabasePlayer databasePlayer) {
            WeeklyBlessings currentWeeklyBlessings = WeeklyBlessings.currentWeeklyBlessings;
            if (currentWeeklyBlessings == null) {
                player.sendMessage(ChatColor.RED + "There are no weekly blessings available at this time.");
                return;
            }
            DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
            Map<Integer, Integer> playerOrder = currentWeeklyBlessings.getPlayerOrders().getOrDefault(player.getUniqueId(), new HashMap<>());

            Menu menu = new Menu("Buy a Blessing", 9 * 4);
            for (int tier = 1; tier <= 5; tier++) {
                int stock = currentWeeklyBlessings.getStock().getOrDefault(tier, 0) - playerOrder.getOrDefault(tier, 0);
                int finalTier = tier;
                List<String> lore = COSTS_LORE.get(tier);
                menu.setItem(tier + 1, 1,
                        new ItemBuilder(Material.PAPER)
                                .name(ChatColor.GREEN + "Tier " + tier)
                                .lore(
                                        ChatColor.GRAY + "Stock: " + ChatColor.YELLOW + stock
                                )
                                .addLore(lore)
                                .amount(tier)
                                .enchant(Enchantment.OXYGEN, 1)
                                .flags(ItemFlag.HIDE_ENCHANTS)
                                .get(),
                        (m, e) -> {
                            if (stock <= 0) {
                                player.sendMessage(ChatColor.RED + "This blessing is out of stock!");
                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 2, 0.5f);
                                return;
                            }
                            LinkedHashMap<Spendable, Long> tierCosts = COSTS.get(finalTier);
                            for (Map.Entry<Spendable, Long> spendableIntegerEntry : tierCosts.entrySet()) {
                                Spendable spendable = spendableIntegerEntry.getKey();
                                Long cost = spendableIntegerEntry.getValue();
                                if (spendable.getFromPlayer(databasePlayer) < cost) {
                                    player.sendMessage(ChatColor.RED + "You need " + spendable.getCostColoredName(cost) + ChatColor.RED + " to bless this item!");
                                    player.playSound(player.getLocation(), Sound.VILLAGER_NO, 2, 0.5f);
                                    return;
                                }
                            }
                            Menu.openConfirmationMenu(
                                    player,
                                    "Buy Blessing",
                                    3,
                                    new ArrayList<>(lore) {{
                                        add(0, ChatColor.GRAY + "Buy " + ChatColor.GREEN + "Tier " + finalTier + ChatColor.GRAY + " Blessing");
                                    }},
                                    Collections.singletonList(ChatColor.GRAY + "Go back"),
                                    (m2, e2) -> {
                                        currentWeeklyBlessings.addPlayerOrder(player.getUniqueId(), finalTier);
                                        tierCosts.forEach((spendable, cost) -> spendable.subtractFromPlayer(databasePlayer, cost));
                                        pveStats.getItemsManager().addBlessingBought(finalTier);
                                        player.playSound(player.getLocation(), Sound.LEVEL_UP, 2, 1.5f);
                                        player.closeInventory();

                                        AbstractItem.sendItemMessage(player, ChatColor.GRAY + "You bought a " +
                                                ChatColor.GREEN + "Tier " + finalTier + ChatColor.GRAY + " Blessing!"
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
                            .name(ChatColor.GREEN + "Tier " + (blessing + 1) + (blessingFound ? " Found" : " Bought") + " Blessing")
                            .addLore(blessingFound ? menuData.getBlessingCurseFoundLore() : menuData.getBlessingCurseBoughtLore())
                            .addLore(
                                    "",
                                    ChatColor.YELLOW.toString() + ChatColor.BOLD + "CLICK" + ChatColor.GREEN + " to select a different blessing"
                            );
                    if (!blessingFound) {
                        selectedBlessing.enchant(Enchantment.OXYGEN, 1);
                        selectedBlessing.flags(ItemFlag.HIDE_ENCHANTS);
                    }
                } else {
                    selectedBlessing = new ItemBuilder(Material.PAPER)
                            .name(ChatColor.YELLOW.toString() + ChatColor.BOLD + "CLICK" + ChatColor.GREEN + " to select a blessing");
                }
            } else {
                selectedBlessing = new ItemBuilder(Material.EMPTY_MAP)
                        .name(ChatColor.RED + "Select an Item first");
            }


            Menu menu = new Menu("Apply a Blessing", 9 * 6);
            ItemMenuUtil.addItemTierRequirement(
                    menu,
                    ItemTier.ALL,
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
                            player.sendMessage(ChatColor.RED + "Select an Item first!");
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
                    player, "Select an Item",
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
                            "",
                            ChatColor.YELLOW.toString() + ChatColor.BOLD + "CLICK" + ChatColor.GREEN + " to select"
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
                            .name(ChatColor.GREEN + "Unknown Blessings")
                            .lore(
                                    WordWrap.wrapWithNewline(ChatColor.GRAY + "You can find unknown blessings by killing mobs.", 150),
                                    "",
                                    ChatColor.GREEN + "Bought Blessings",
                                    WordWrap.wrapWithNewline(ChatColor.GRAY + "You can buy blessings through Michael. " +
                                            "Bought blessings have a guaranteed chance of applying its tier.", 150)
                            )
                            .get(),
                    (m, e) -> {
                    }
            );
            int blessingsFound = itemsManager.getBlessingsFound();
            menu.setItem(2, 1,
                    new ItemBuilder(Material.PAPER)
                            .name(ChatColor.GREEN + "Unknown Blessings")
                            .lore(
                                    ChatColor.GRAY + "Amount: " + ChatColor.YELLOW + blessingsFound,
                                    "",
                                    ChatColor.GRAY + "Bless Chance: " +
                                            ChatColor.YELLOW + NumberFormat.formatOptionalTenths(menuData.getItem().getTier().blessedChance * 100) + "%",
                                    ChatColor.GRAY + "Curse Chance: " +
                                            ChatColor.YELLOW + NumberFormat.formatOptionalTenths(menuData.getItem().getTier().cursedChance * 100) + "%",
                                    "",
                                    blessingsFound > 0 ?
                                    ChatColor.YELLOW.toString() + ChatColor.BOLD + "CLICK" + ChatColor.GREEN + " to select" :
                                    ChatColor.RED + "You have no unknown blessings"
                            )
                            .amount(blessingsFound)
                            .get(),
                    (m, e) -> {
                        if (blessingsFound <= 0) {
                            player.sendMessage(ChatColor.RED + "You have no unknown blessings!");
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
                        .name(ChatColor.GREEN + "Tier " + tier)
                        .lore(
                                ChatColor.GRAY + "Amount: " + ChatColor.YELLOW + blessingBoughtAmount,
                                "",
                                ChatColor.GRAY + "Bless Chance: " + ChatColor.YELLOW + "100%",
                                ChatColor.GRAY + "Curse Chance: " + ChatColor.YELLOW + "0%",
                                "",
                                normalItem ? blessingBoughtAmount > 0 ?
                                             ChatColor.YELLOW.toString() + ChatColor.BOLD + "CLICK" + ChatColor.GREEN + " to select" :
                                             ChatColor.RED + "You have no blessings of this tier" :
                                ChatColor.RED + "Only applicable to non blessed/cursed items"
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
                                player.sendMessage(ChatColor.RED + "Only applicable to non blessed/cursed items");
                                return;
                            }
                            if (blessingBoughtAmount <= 0) {
                                player.sendMessage(ChatColor.RED + "You have no blessings of this tier!");
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
                    .name(ChatColor.GREEN + "Click to Apply Blessing")
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

                        Menu.openConfirmationMenu(player,
                                "Confirm Item Blessing",
                                3,
                                new ArrayList<>() {{
                                    add(ChatColor.GRAY + "Apply Blessing");
                                    addAll(blessingFound ? menuData.getBlessingCurseFoundLore() : menuData.getBlessingCurseBoughtLore());
                                }},
                                Collections.singletonList(ChatColor.GRAY + "Go back"),
                                (m2, e2) -> {
                                    int tier = blessing + 1;
                                    ComponentBuilder componentBuilder = new ComponentBuilder(ChatColor.GRAY + "You applied a" + (blessingFound ? "n unknown" : " bought tier " + tier) + " blessing on ")
                                            .appendHoverItem(item.getItemName(), item.generateItemStack())
                                            .append(ChatColor.GRAY + " and it became ");

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
                                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 2, 2);
                                    player.closeInventory();

                                    AbstractItem.sendItemMessage(player, componentBuilder.appendHoverItem(item.getItemName(), item.generateItemStack()));
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

            public List<String> getBlessingCurseFoundLore() {
                return new ArrayList<>() {{
                    add("");
                    add(ChatColor.WHITE + "Dud Chance" + ChatColor.GRAY + " - " +
                            ChatColor.YELLOW + NumberFormat.formatOptionalHundredths((1 - item.getTier().blessedChance - item.getTier().cursedChance) * 100) + "%");
                    add("");
                    add(ChatColor.DARK_GREEN + "Bless Chance" + ChatColor.GRAY + " - " +
                            ChatColor.YELLOW + NumberFormat.formatOptionalHundredths(item.getTier().blessedChance * 100) + "%");
                    for (int i = 1; i <= 5; i++) {
                        add("   " + getModifiedLore(i, ItemModifier.BLESSING_TIER_CHANCE.get(i)).replaceAll("\n", "\n     "));
                    }
                    add("");
                    add(ChatColor.DARK_RED + "Curse Chance" + ChatColor.GRAY + " - " +
                            ChatColor.YELLOW + NumberFormat.formatOptionalHundredths(item.getTier().cursedChance * 100) + "%");
                    for (int i = 1; i <= 5; i++) {
                        add("   " + getModifiedLore(-i, ItemModifier.CURSE_TIER_CHANCE.get(i)).replaceAll("\n", "\n     "));
                    }
                }};
            }

            private String getModifiedLore(int newModifier, double chance) {
                if (newModifier == 0) {
                    return ChatColor.WHITE + "Normal" + ChatColor.GRAY + " - " + ChatColor.YELLOW + NumberFormat.formatOptionalHundredths(item.getTier().blessedChance * 100) + "%";
                } else {
                    ItemModifier itemModifier = item.getItemModifier(newModifier);
                    boolean isBlessing = newModifier > 0;
                    return (isBlessing ? ChatColor.GREEN : ChatColor.RED) + itemModifier.getName() + ChatColor.GRAY + " - " +
                            ChatColor.YELLOW + (blessingFound ? NumberFormat.formatOptionalHundredths(chance) : (isBlessing ? "100" : "0")) + "%" +
                            "\n " + ChatColor.GREEN + itemModifier.getDescription();

                }
            }

            public List<String> getBlessingCurseBoughtLore() {
                if (!blessingFound) {
                    ItemModifier itemBlessing = item.getBlessings()[blessing];
                    return Arrays.asList(
                            "",
                            ChatColor.GREEN + itemBlessing.getName() + ChatColor.GRAY + " - " +
                                    ChatColor.YELLOW + (blessingFound ? NumberFormat.formatOptionalHundredths(item.getTier().blessedChance * 100) : "100") + "%",
                            "  " + ChatColor.GREEN + itemBlessing.getDescription()
                    );
                }
                int modifier = item.getModifier();
                int tier = blessing + 1;
                return Arrays.asList(
                        "",
                        getModifiedLore(Math.min(modifier + tier, 5), item.getTier().blessedChance * 100),
                        "",
                        getModifiedLore(Math.max(modifier - tier, -5), item.getTier().cursedChance * 100)
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
                    ItemTier.ALL,
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
                            "",
                            ChatColor.YELLOW.toString() + ChatColor.BOLD + "CLICK" + ChatColor.GREEN + " to select"
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
                            .name(ChatColor.GREEN + "Click to Purify Item")
                            .lore(
                                    ItemMenuUtil.getRequirementMetString(item != null, "Item Selected"),
                                    ItemMenuUtil.getRequirementMetString(enoughCost, "Enough Loot")
                            )
                            .get(),
                    (m, e) -> {
                        if (item == null) {
                            player.sendMessage(ChatColor.RED + "Select an Item first!");
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 2, 0.5f);
                            return;
                        }
                        LinkedHashMap<Spendable, Long> removeCurseCost = item.getTier().removeCurseCost;
                        for (Map.Entry<Spendable, Long> spendableLongEntry : removeCurseCost.entrySet()) {
                            Spendable spendable = spendableLongEntry.getKey();
                            Long cost = spendableLongEntry.getValue();
                            if (spendable.getFromPlayer(databasePlayer) < cost) {
                                player.sendMessage(ChatColor.RED + "You need " + spendable.getCostColoredName(cost) + ChatColor.RED + " to purify this Item!");
                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 2, 0.5f);
                                return;
                            }
                        }


                        Menu.openConfirmationMenu(player,
                                "Confirm Purification",
                                3,
                                new ArrayList<>() {{
                                    if (item.getModifier() == -1) {
                                        add(ChatUtils.addStrikeThrough(item.getCurses()[-item.getModifier() - 1].getDescription()));
                                    } else {
                                        add(item.getCurses()[-item.getModifier() - 1].getDescription() + ChatColor.DARK_GREEN + " > " + item.getCurses()[-item.getModifier() - 2].getDescription());
                                    }
                                    addAll(PvEUtils.getCostLore(removeCurseCost, true));
                                }},
                                Collections.singletonList(ChatColor.GRAY + "Go back"),
                                (m2, e2) -> {
                                    ComponentBuilder componentBuilder = new ComponentBuilder(ChatColor.GRAY + "You decreased the tier of curse from ")
                                            .appendHoverItem(item.getItemName(), item.generateItemStack())
                                            .append(ChatColor.GRAY + " and it became ");

                                    for (Map.Entry<Spendable, Long> spendableLongEntry : removeCurseCost.entrySet()) {
                                        spendableLongEntry.getKey().subtractFromPlayer(databasePlayer, spendableLongEntry.getValue());
                                    }
                                    item.setModifier(item.getModifier() + 1);
                                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                                    player.playSound(player.getLocation(), Sound.SPLASH, 2, 0.1f);
                                    player.closeInventory();

                                    AbstractItem.sendItemMessage(player, componentBuilder.appendHoverItem(item.getItemName(), item.generateItemStack()));
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
