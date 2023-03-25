package com.ebicep.warlords.pve.items.menu;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.items.pojos.WeeklyBlessings;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.items.ItemsManager;
import com.ebicep.warlords.pve.items.modifiers.ItemModifier;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.mobs.MobDrops;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.java.NumberFormat;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

import static com.ebicep.warlords.menu.Menu.MENU_BACK;

public class ItemMichaelMenu {

    public static void openMichaelItemMenu(Player player, DatabasePlayer databasePlayer) {
        Menu menu = new Menu("Mysterious Michael", 9 * 4);

        menu.setItem(1, 1,
                new ItemBuilder(Material.BOOK)
                        .name(ChatColor.GREEN + "Your Blessings")
                        .lore()
                        .get(),
                (m, e) -> {
                    YourBlessingsMenu.openYourBlessingsMenu(player, databasePlayer);
                }
        );

        menu.setItem(3, 1,
                new ItemBuilder(Material.PAPER)
                        .name(ChatColor.GREEN + "Buy a Blessing")
                        .get(),
                (m, e) -> {
                    BuyABlessingMenu.openBuyABlessingMenu(player, databasePlayer);
                }
        );
        menu.setItem(5, 1,
                new ItemBuilder(Material.ANVIL)
                        .name(ChatColor.GREEN + "Apply a Blessing")
                        .get(),
                (m, e) -> {
                    ApplyBlessingMenu.openApplyBlessingMenu(player, databasePlayer, new ApplyBlessingMenu.ApplyBlessingMenuData());

                }
        );
        menu.setItem(7, 1,
                new ItemBuilder(Material.FIREBALL)
                        .name(ChatColor.GREEN + "Remove a Curse")
                        .get(),
                (m, e) -> {
                    RemoveCurseMenu.openRemoveACurseMenu(player, databasePlayer, null);
                }
        );

        menu.setItem(4, 3, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static class YourBlessingsMenu {

        public static void openYourBlessingsMenu(Player player, DatabasePlayer databasePlayer) {
            Menu menu = new Menu("Your Blessings", 9 * 5);

            ItemsManager itemsManager = databasePlayer.getPveStats().getItemsManager();
            menu.setItem(1, 1,
                    new ItemBuilder(Material.BOOK)
                            .name(ChatColor.GREEN + "Found Blessings")
                            .lore(WordWrap.wrapWithNewline(ChatColor.GRAY + "You can find blessings by killing mobs.", 150))
                            .amount(itemsManager.getBlessingsFound().values().stream().mapToInt(Integer::intValue).sum())
                            .get(),
                    (m, e) -> {
                    }
            );
            menu.setItem(1, 2,
                    new ItemBuilder(Material.BOOK)
                            .name(ChatColor.GREEN + "Bought Blessings")
                            .lore(WordWrap.wrapWithNewline(ChatColor.GRAY + "You can buy blessings through Michael. " +
                                    "Bought blessings have a guaranteed chance of applying its tier.", 150))
                            .amount(itemsManager.getBlessingsBought().values().stream().mapToInt(Integer::intValue).sum())
                            .get(),
                    (m, e) -> {
                    }
            );
            for (int tier = 1; tier <= 5; tier++) {
                menu.setItem(tier + 1, 1,
                        new ItemBuilder(Material.PAPER)
                                .name(ChatColor.GREEN + "Tier " + tier)
                                .lore(ChatColor.GRAY + "Amount: " + ChatColor.YELLOW + itemsManager.getBlessingFoundAmount(tier))
                                .amount(tier)
                                .get(),
                        (m, e) -> {

                        }
                );
                menu.setItem(tier + 1, 2,
                        new ItemBuilder(Material.PAPER)
                                .name(ChatColor.GREEN + "Tier " + tier)
                                .lore(ChatColor.GRAY + "Amount: " + ChatColor.YELLOW + itemsManager.getBlessingBoughtAmount(tier))
                                .amount(tier)
                                .get(),
                        (m, e) -> {

                        }
                );
            }

            menu.setItem(4, 4, MENU_BACK, (m, e) -> openMichaelItemMenu(player, databasePlayer));
            menu.openForPlayer(player);
        }

    }

    public static class BuyABlessingMenu {

        private static final HashMap<Integer, LinkedHashMap<MobDrops, Integer>> COSTS = new HashMap<>();
        private static final HashMap<Integer, List<String>> COSTS_LORE = new HashMap<>();

        public static void initializeCosts(WeeklyBlessings weeklyBlessings) {
            weeklyBlessings.getZenithCosts().forEach((tier, stars) -> {
                COSTS.computeIfAbsent(tier, k -> new LinkedHashMap<>()).put(MobDrops.ZENITH_STAR, stars);
            });
            COSTS.get(4).put(MobDrops.CELESTIAL_BRONZE, 5);
            COSTS.get(5).put(MobDrops.CELESTIAL_BRONZE, 10);

            COSTS.forEach((tier, mobDropCosts) -> {
                List<String> lore = COSTS_LORE.computeIfAbsent(tier, k -> new ArrayList<>());
                lore.add("");
                lore.add(ChatColor.AQUA + "Cost: ");
                mobDropCosts.forEach((currency, amount) -> lore.add(ChatColor.GRAY + " - " + currency.getCostColoredName(amount)));
            });
        }

        public static void openBuyABlessingMenu(Player player, DatabasePlayer databasePlayer) {
            WeeklyBlessings currentWeeklyBlessings = WeeklyBlessings.currentWeeklyBlessings;
            if (currentWeeklyBlessings == null) {
                player.sendMessage(ChatColor.RED + "There are no weekly blessings available at this time.");
                return;
            }
            DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
            Map<Integer, Integer> playerOrder = currentWeeklyBlessings.getPlayerOrders().computeIfAbsent(player.getUniqueId(), k -> new HashMap<>());

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
                                .get(),
                        (m, e) -> {
                            if (stock <= 0) {
                                player.sendMessage(ChatColor.RED + "This blessing is out of stock!");
                                return;
                            }
                            LinkedHashMap<MobDrops, Integer> tierCosts = COSTS.get(finalTier);
                            for (Map.Entry<MobDrops, Integer> currenciesLongEntry : tierCosts.entrySet()) {
                                MobDrops mobDrop = currenciesLongEntry.getKey();
                                Integer cost = currenciesLongEntry.getValue();
                                if (pveStats.getMobDrops(mobDrop) < cost) {
                                    player.sendMessage(ChatColor.RED + "You need " + mobDrop.getCostColoredName(cost) + ChatColor.RED + " to bless this item!");
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
                                        tierCosts.forEach((mobDrops, cost) -> pveStats.addMobDrops(mobDrops, -cost));
                                        currentWeeklyBlessings.addPlayerOrder(player.getUniqueId(), finalTier);
                                        pveStats.getItemsManager().addBlessingBought(finalTier);
                                        pveStats.getItemsManager().addBlessing(finalTier);
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
            menu.openForPlayer(player);
        }

    }

    public static class ApplyBlessingMenu {

        private static final LinkedHashMap<MobDrops, Long> COST = new LinkedHashMap<>();

        public static void openApplyBlessingMenu(Player player, DatabasePlayer databasePlayer, ApplyBlessingMenuData menuData) {
            AbstractItem<?, ?, ?> item = menuData.getItem();
            Integer blessing = menuData.getBlessing();
            ItemStack selectedItem;
            ItemStack selectedBlessing;
            if (item != null) {
                selectedItem = new ItemBuilder(item.generateItemStack())
                        .addLore(
                                "",
                                ChatColor.YELLOW.toString() + ChatColor.BOLD + "CLICK" + ChatColor.GREEN + " to select a different Item"
                        )
                        .get();
                if (blessing != null) {
                    ItemModifier<?> itemBlessing = item.getBlessings()[blessing];
                    ItemModifier<?> itemCurse = item.getCurses()[blessing];
                    int tier = blessing + 1;
                    selectedBlessing = new ItemBuilder(Material.PAPER)
                            .name(ChatColor.GREEN + "Tier " + tier + " Blessing")
                            .addLore(
                                    "",
                                    ChatColor.GREEN + itemBlessing.getName() + ChatColor.GRAY + " - " +
                                            ChatColor.YELLOW + NumberFormat.formatOptionalTenths(ItemModifier.BLESSING_TIER_CHANCE.get(tier)) + "%",
                                    "  " + ChatColor.GREEN + itemBlessing.getDescription(),
                                    "",
                                    ChatColor.RED + itemCurse.getName() + ChatColor.GRAY + " - " +
                                            ChatColor.YELLOW + NumberFormat.formatOptionalTenths(ItemModifier.CURSE_TIER_CHANCE.get(tier)) + "%",
                                    "  " + ChatColor.GREEN + itemCurse.getDescription(),
                                    "",
                                    ChatColor.YELLOW.toString() + ChatColor.BOLD + "CLICK" + ChatColor.GREEN + " to select a different blessing"
                            )
                            .get();
                } else {
                    selectedBlessing = new ItemBuilder(Material.PAPER)
                            .name(ChatColor.YELLOW.toString() + ChatColor.BOLD + "CLICK" + ChatColor.GREEN + " to select a blessing")
                            .get();
                }
            } else {
                selectedItem = new ItemBuilder(Material.SKULL_ITEM)
                        .name(ChatColor.YELLOW.toString() + ChatColor.BOLD + "CLICK" + ChatColor.GREEN + " to select an Item")
                        .get();
                selectedBlessing = new ItemBuilder(Material.EMPTY_MAP)
                        .name(ChatColor.RED + "Select an Item first")
                        .get();
            }


            Menu menu = new Menu("Apply a Blessing", 9 * 3);
            menu.setItem(1, 0,
                    selectedItem,
                    (m, e) -> {
                        openApplyBlessingItemSelectMenu(
                                player,
                                databasePlayer,
                                new ItemMenu.PlayerItemMenuSettings(databasePlayer)
                                        .setItemInventory(databasePlayer.getPveStats()
                                                                        .getItemsManager()
                                                                        .getItemInventory()
                                                                        .stream()
                                                                        .filter(i -> !i.isModified())
                                                                        .collect(Collectors.toList())),
                                menuData
                        );
                    }
            );
            ItemMenuUtil.addPaneRequirement(menu, 2, 0, item != null);
            menu.setItem(1, 1,
                    selectedBlessing,
                    (m, e) -> {
                        if (item == null) {
                            player.sendMessage(ChatColor.RED + "Select an Item first!");
                            return;
                        }
                        openApplyBlessingBlessingSelectMenu(player, databasePlayer, menuData);
                    }
            );
            ItemMenuUtil.addPaneRequirement(menu, 2, 1, blessing != null);
            ItemMenuUtil.addMobDropRequirement(databasePlayer, menu, new LinkedHashMap<>(), 1, 2);

            ItemMenuUtil.addItemConfirmation(menu, () -> {
                addCraftItemConfirmation(player, databasePlayer, menuData, menu);
            });

            menu.setItem(4, 2, MENU_BACK, (m, e) -> openMichaelItemMenu(player, databasePlayer));
            menu.openForPlayer(player);
        }

        private static void openApplyBlessingItemSelectMenu(
                Player player,
                DatabasePlayer databasePlayer,
                ItemMenu.PlayerItemMenuSettings menuSettings,
                ApplyBlessingMenuData menuData
        ) {
            ItemMenu menu = new ItemMenu(
                    player, "Select an Item",
                    (i, m, e) -> {
                        menuData.setItem(i);
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

        private static void openApplyBlessingBlessingSelectMenu(
                Player player,
                DatabasePlayer databasePlayer,
                ApplyBlessingMenuData menuData
        ) {
            Menu menu = new Menu("Select a Blessing Tier", 9 * 5);
            ItemsManager itemsManager = databasePlayer.getPveStats().getItemsManager();
            menu.setItem(1, 1,
                    new ItemBuilder(Material.BOOK)
                            .name(ChatColor.GREEN + "Found Blessings")
                            .lore(WordWrap.wrapWithNewline(ChatColor.GRAY + "Chance of blessing, cursing, or doing nothing to an Item.", 150))
                            .amount(itemsManager.getBlessingsFound().values().stream().mapToInt(Integer::intValue).sum())
                            .get(),
                    (m, e) -> {
                    }
            );
            menu.setItem(1, 2,
                    new ItemBuilder(Material.BOOK)
                            .name(ChatColor.GREEN + "Bought Blessings")
                            .lore(WordWrap.wrapWithNewline(ChatColor.GRAY + "Guaranteed chance of blessing an Item.", 150))
                            .amount(itemsManager.getBlessingsBought().values().stream().mapToInt(Integer::intValue).sum())
                            .get(),
                    (m, e) -> {
                    }
            );
            for (int tier = 1; tier <= 5; tier++) {
                int finalTier = tier;
                Integer blessingFoundAmount = itemsManager.getBlessingFoundAmount(tier);
                Integer blessingBoughtAmount = itemsManager.getBlessingBoughtAmount(tier);
                menu.setItem(tier + 1, 1,
                        new ItemBuilder(Material.PAPER)
                                .name(ChatColor.GREEN + "Tier " + tier)
                                .lore(
                                        ChatColor.GRAY + "Amount: " + ChatColor.YELLOW + blessingFoundAmount,
                                        "",
                                        ChatColor.GRAY + "Bless Chance: " +
                                                ChatColor.YELLOW + NumberFormat.formatOptionalTenths(ItemModifier.BLESSING_TIER_CHANCE.get(tier)) + "%",
                                        ChatColor.GRAY + "Curse Chance: " +
                                                ChatColor.YELLOW + NumberFormat.formatOptionalTenths(ItemModifier.CURSE_TIER_CHANCE.get(tier)) + "%",
                                        "",
                                        blessingFoundAmount > 0 ?
                                        ChatColor.YELLOW.toString() + ChatColor.BOLD + "CLICK" + ChatColor.GREEN + " to select" :
                                        ChatColor.RED + "You have no blessings of this tier"
                                )
                                .amount(tier)
                                .get(),
                        (m, e) -> {
                            if (blessingFoundAmount <= 0) {
                                player.sendMessage(ChatColor.RED + "You have no blessings of this tier!");
                                return;
                            }
                            menuData.setBlessing(finalTier - 1);
                            menuData.setBlessingFound(true);
                            openApplyBlessingMenu(player, databasePlayer, menuData);
                        }
                );
                menu.setItem(tier + 1, 2,
                        new ItemBuilder(Material.PAPER)
                                .name(ChatColor.GREEN + "Tier " + tier)
                                .lore(
                                        ChatColor.GRAY + "Amount: " + ChatColor.YELLOW + blessingBoughtAmount,
                                        "",
                                        ChatColor.GRAY + "Bless Chance: " + ChatColor.YELLOW + "100%",
                                        ChatColor.GRAY + "Curse Chance: " + ChatColor.YELLOW + "0%",
                                        "",
                                        blessingBoughtAmount > 0 ?
                                        ChatColor.YELLOW.toString() + ChatColor.BOLD + "CLICK" + ChatColor.GREEN + " to select" :
                                        ChatColor.RED + "You have no blessings of this tier"
                                )
                                .amount(tier)
                                .get(),
                        (m, e) -> {
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
            menu.setItem(4, 4,
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
            AbstractItem<?, ?, ?> item = menuData.getItem();
            Integer blessing = menuData.getBlessing();
            ItemBuilder itemBuilder;
            boolean enoughMobDrops = COST.entrySet()
                                         .stream()
                                         .allMatch(entry -> pveStats.getMobDrops(entry.getKey()) >= entry.getValue());
            if (item != null && blessing != null && enoughMobDrops) {
                itemBuilder = item.generateItemBuilder()
                                  .addLore(
                                          "",
                                          ChatColor.YELLOW.toString() + ChatColor.BOLD + "CLICK" + ChatColor.GREEN + " to Apply Blessing"
                                  );
            } else {
                itemBuilder = new ItemBuilder(Material.BARRIER)
                        .name(ChatColor.GREEN + "Click to apply Blessing")
                        .lore(
                                ItemMenuUtil.getRequirementMetString(item != null, "Item Selected"),
                                ItemMenuUtil.getRequirementMetString(blessing != null, "Blessing Selected"),
                                ItemMenuUtil.getRequirementMetString(enoughMobDrops, "Enough Mob Drops")
                        );
            }
            menu.setItem(7, 1,
                    itemBuilder.get(),
                    (m, e) -> {
                        if (item == null || blessing == null || !enoughMobDrops) {
                            return;
                        }

                        Menu.openConfirmationMenu(player,
                                "Confirm Item Blessing",
                                3,
                                Collections.singletonList(ChatColor.GRAY + "Apply Blessing"),
                                Collections.singletonList(ChatColor.GRAY + "Go back"),
                                (m2, e2) -> {
                                    for (Map.Entry<MobDrops, Long> currenciesLongEntry : COST.entrySet()) {
                                        currenciesLongEntry.getKey().subtractFromPlayer(databasePlayer, currenciesLongEntry.getValue());
                                    }

                                    ComponentBuilder componentBuilder = new ComponentBuilder(ChatColor.GRAY + "You blessed ")
                                            .appendHoverItem(item.getName(), item.generateItemStack())
                                            .append(ChatColor.GRAY + " and it became ");

                                    item.setModifier(blessing);
                                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);

                                    AbstractItem.sendItemMessage(player, componentBuilder.appendHoverItem(item.getName(), item.generateItemStack()));
                                    player.closeInventory();
                                },
                                (m2, e2) -> openApplyBlessingMenu(player, databasePlayer, menuData),
                                (m2) -> {
                                }
                        );
                    }
            );
        }

        private static class ApplyBlessingMenuData {
            private AbstractItem<?, ?, ?> item;
            private Integer blessing;
            private boolean blessingFound;

            public ApplyBlessingMenuData() {
            }

            public AbstractItem<?, ?, ?> getItem() {
                return item;
            }

            public void setItem(AbstractItem<?, ?, ?> item) {
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

    public static class RemoveCurseMenu {

        private static final LinkedHashMap<MobDrops, Long> COST = new LinkedHashMap<>();

        public static void openRemoveACurseMenu(Player player, DatabasePlayer databasePlayer, AbstractItem<?, ?, ?> item) {
            ItemStack selectedItem;
            if (item != null) {
                selectedItem = new ItemBuilder(item.generateItemStack())
                        .addLore(
                                "",
                                ChatColor.YELLOW.toString() + ChatColor.BOLD + "CLICK" + ChatColor.GREEN + " to select a different item"
                        )
                        .get();
            } else {
                selectedItem = new ItemBuilder(Material.SKULL_ITEM)
                        .name(ChatColor.YELLOW.toString() + ChatColor.BOLD + "CLICK" + ChatColor.GREEN + " to select an item")
                        .get();
            }

            Menu menu = new Menu("Remove a Curse", 9 * 3);
            menu.setItem(0, 1,
                    selectedItem,
                    (m, e) -> {
                        openRemoveCurseItemSelectMenu(
                                player,
                                databasePlayer,
                                new ItemMenu.PlayerItemMenuSettings(databasePlayer)
                                        .setItemInventory(databasePlayer.getPveStats()
                                                                        .getItemsManager()
                                                                        .getItemInventory()
                                                                        .stream()
                                                                        .filter(i -> i.getModifier() < 0)
                                                                        .collect(Collectors.toList())),
                                item
                        );
                    }
            );
            ItemMenuUtil.addPaneRequirement(menu, 1, 1, item != null);
            ItemMenuUtil.addMobDropRequirement(databasePlayer, menu, COST, 2, 1);
            ItemMenuUtil.addItemConfirmation(menu, () -> {
                addRemoveCurseItemConfirmation(player, databasePlayer, item, menu);
            });

            menu.setItem(4, 2, MENU_BACK, (m, e) -> openMichaelItemMenu(player, databasePlayer));
            menu.openForPlayer(player);
        }

        private static void openRemoveCurseItemSelectMenu(
                Player player,
                DatabasePlayer databasePlayer,
                ItemMenu.PlayerItemMenuSettings menuSettings,
                AbstractItem<?, ?, ?> item
        ) {
            ItemMenu menu = new ItemMenu(
                    player, "Select an Item",
                    (i, m, e) -> {
                        openRemoveACurseMenu(player, databasePlayer, i);
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
                                openRemoveACurseMenu(player, databasePlayer, item);
                            }
                    )
            );

            menu.open();
        }

        private static void addRemoveCurseItemConfirmation(
                Player player,
                DatabasePlayer databasePlayer,
                AbstractItem<?, ?, ?> item,
                Menu menu
        ) {
            DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
            ItemBuilder itemBuilder;
            boolean enoughMobDrops = COST.entrySet()
                                         .stream()
                                         .allMatch(entry -> pveStats.getMobDrops(entry.getKey()) >= entry.getValue());
            if (item != null && enoughMobDrops) {
                itemBuilder = new ItemBuilder(item.clone().setModifier(0).generateItemStack())
                        .addLore(
                                "",
                                ChatColor.YELLOW.toString() + ChatColor.BOLD + "CLICK" + ChatColor.GREEN + " to remove Curse"
                        );
                ;
            } else {
                itemBuilder = new ItemBuilder(Material.BARRIER)
                        .name(ChatColor.GREEN + "Click to Remove Curse");
            }
            menu.setItem(7, 1,
                    itemBuilder.get(),
                    (m, e) -> {
                        if (item == null) {
                            player.sendMessage(ChatColor.RED + "Select an Item first!");
                            return;
                        }
                        for (Map.Entry<MobDrops, Long> currenciesLongEntry : COST.entrySet()) {
                            MobDrops mobDrop = currenciesLongEntry.getKey();
                            Long cost = currenciesLongEntry.getValue();
                            if (pveStats.getMobDrops(mobDrop) < cost) {
                                player.sendMessage(ChatColor.RED + "You need " + mobDrop.getCostColoredName(cost) + ChatColor.RED + " to bless this item!");
                                return;
                            }
                        }

                        Menu.openConfirmationMenu(player,
                                "Confirm Remove Curse",
                                3,
                                Collections.singletonList(ChatColor.GRAY + "Remove Curse"),
                                Collections.singletonList(ChatColor.GRAY + "Go back"),
                                (m2, e2) -> {
                                    for (Map.Entry<MobDrops, Long> currenciesLongEntry : COST.entrySet()) {
                                        currenciesLongEntry.getKey().subtractFromPlayer(databasePlayer, currenciesLongEntry.getValue());
                                    }

                                    ComponentBuilder componentBuilder = new ComponentBuilder(ChatColor.GRAY + "You remove the curse from ")
                                            .appendHoverItem(item.getName(), item.generateItemStack())
                                            .append(ChatColor.GRAY + " and it became ");

                                    item.setModifier(0);
                                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);

                                    AbstractItem.sendItemMessage(player, componentBuilder.appendHoverItem(item.getName(), item.generateItemStack()));
                                    player.closeInventory();
                                },
                                (m2, e2) -> openRemoveACurseMenu(player, databasePlayer, item),
                                (m2) -> {
                                }
                        );
                    }
            );
        }
    }
}
