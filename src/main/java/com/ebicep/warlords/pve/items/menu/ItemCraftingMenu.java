package com.ebicep.warlords.pve.items.menu;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.menu.util.ItemMenuUtil;
import com.ebicep.warlords.pve.items.menu.util.ItemSearchMenu;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.items.types.ItemType;
import com.ebicep.warlords.pve.mobs.MobDrops;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.java.TriConsumer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class ItemCraftingMenu {

    private static final HashMap<ItemTier, TierCostInfo> TIER_COST_INFO = new HashMap<>() {{
        put(ItemTier.DELTA, new TierCostInfo(
                new LinkedHashMap<>() {{
                    put(MobDrops.ZENITH_STAR, 1L);
                }},
                new Pair<>(2, 2),
                new ArrayList<>() {{
                    add(new TierRequirement(ItemTier.ALPHA, 0, 0));
                    add(new TierRequirement(ItemTier.BETA, 2, 0));
                    add(new TierRequirement(ItemTier.GAMMA, 0, 2));
                }}
        ));
        put(ItemTier.OMEGA, new TierCostInfo(
                new LinkedHashMap<>() {{
                    put(MobDrops.ZENITH_STAR, 1L);
                    put(MobDrops.CELESTIAL_BRONZE, 1L);
                }},
                new Pair<>(2, 1),
                new ArrayList<>() {{
                    add(new TierRequirement(ItemTier.DELTA, 0, 1));
                }}
        ));
    }};

    public static void openItemCraftingMenu(Player player, HashMap<ItemTier, AbstractItem<?, ?, ?>> items) {
        DatabaseManager.getPlayer(player.getUniqueId(), databasePlayer -> {
            Menu menu = new Menu("Select Item to Craft", 9 * 4);

            menu.setItem(2, 1,
                    new ItemBuilder(Material.BREWING_STAND_ITEM)
                            .name(ChatColor.GREEN + "Delta Forging")
                            .lore(ChatColor.GRAY + "Craft a Delta Tiered Item")
                            .get(),
                    (m, e) -> openForgingMenu(player, databasePlayer, ItemTier.DELTA, items)
            );

            menu.setItem(6, 1,
                    new ItemBuilder(Material.BREWING_STAND_ITEM)
                            .name(ChatColor.GREEN + "Omega Forging")
                            .lore(ChatColor.GRAY + "Craft an Omega Tiered Item")
                            .get(),
                    (m, e) -> openForgingMenu(player, databasePlayer, ItemTier.OMEGA, items)
            );

            menu.setItem(4, 3, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
            menu.openForPlayer(player);
        });
    }

    private static void openForgingMenu(Player player, DatabasePlayer databasePlayer, ItemTier itemTier, HashMap<ItemTier, AbstractItem<?, ?, ?>> items) {
        Menu menu = new Menu(itemTier.name + " Forging", 9 * 3);

        TierCostInfo tierCostInfo = TIER_COST_INFO.get(itemTier);
        List<TierRequirement> requirements = tierCostInfo.getRequirements();
        for (TierRequirement requirement : requirements) {
            ItemTier tier = requirement.getTier();
            ItemMenuUtil.addItemTierRequirement(menu, tier, items.get(tier), requirement.getX(), requirement.getY(), (m, e) -> {
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

        Pair<Integer, Integer> costLocation = tierCostInfo.getCostLocation();
        DatabasePlayerPvE pveStats = databasePlayer.getPveStats();

        ItemMenuUtil.addMobDropRequirement(databasePlayer, menu, tierCostInfo.getCost(), costLocation.getA(), costLocation.getB());
        ItemMenuUtil.addItemConfirmation(menu, () -> {
            addCraftItemConfirmation(player, databasePlayer, items, menu, requirements, pveStats, itemTier);
        });

        menu.setItem(4, 2, Menu.MENU_BACK, (m, e) -> openItemCraftingMenu(player, new HashMap<>()));
        menu.openForPlayer(player);
    }

    private static void openItemSelectMenu(
            Player player,
            DatabasePlayer databasePlayer,
            ItemTier tier,
            BiConsumer<Menu, InventoryClickEvent> back,
            TriConsumer<AbstractItem<?, ?, ?>, Menu, InventoryClickEvent> onClick
    ) {
        ItemSearchMenu menu = new ItemSearchMenu(
                player,
                "Select an Item",
                onClick,
                itemBuilder -> itemBuilder.addLore(
                        "",
                        ChatColor.YELLOW.toString() + ChatColor.BOLD + "CLICK" + ChatColor.GREEN + " to select"
                ),
                new ItemSearchMenu.PlayerItemMenuSettings(databasePlayer)
                        .setItemInventory(databasePlayer.getPveStats()
                                                        .getItemsManager()
                                                        .getItemInventory()
                                                        .stream()
                                                        .filter(item -> item.getTier() == tier)
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
            HashMap<ItemTier, AbstractItem<?, ?, ?>> items,
            Menu menu,
            List<TierRequirement> requirements,
            DatabasePlayerPvE pveStats,
            ItemTier tier
    ) {
        boolean requirementsMet = requirements.stream().allMatch(requirement -> items.get(requirement.getTier()) != null);
        boolean enoughMobDrops = TIER_COST_INFO.get(tier)
                                               .getCost()
                                               .entrySet()
                                               .stream()
                                               .allMatch(entry -> pveStats.getMobDrops(entry.getKey()) >= entry.getValue());
        menu.setItem(7, 1,
                new ItemBuilder(requirementsMet && enoughMobDrops ? tier.clayBlock : new ItemStack(Material.BARRIER))
                        .name(ChatColor.GREEN + "Click to Craft Item")
                        .lore(
                                ItemMenuUtil.getRequirementMetString(requirementsMet, "Required Item" + (requirements.size() != 1 ? "s" : "") + " Selected"),
                                ItemMenuUtil.getRequirementMetString(enoughMobDrops, "Enough Mob Drops")
                        )
                        .get(),
                (m, e) -> {
                    if (!requirementsMet) {
                        player.sendMessage(ChatColor.RED + "You do not have all the required items to craft this item!");
                        return;
                    }
                    TierCostInfo tierCostInfo = TIER_COST_INFO.get(tier);
                    for (Map.Entry<MobDrops, Long> currenciesLongEntry : tierCostInfo.getCost().entrySet()) {
                        MobDrops mobDrop = currenciesLongEntry.getKey();
                        Long cost = currenciesLongEntry.getValue();
                        if (pveStats.getMobDrops(mobDrop) < cost) {
                            player.sendMessage(ChatColor.RED + "You need " + mobDrop.getCostColoredName(cost) + ChatColor.RED + " to craft this item!");
                            return;
                        }
                    }

                    Menu.openConfirmationMenu(player,
                            "Confirm Item Craft",
                            3,
                            Collections.singletonList(ChatColor.GRAY + "Craft " + tier.getColoredName() + ChatColor.GRAY + " Item"),
                            Collections.singletonList(ChatColor.GRAY + "Go back"),
                            (m2, e2) -> {
                                for (TierRequirement requirement : requirements) {
                                    pveStats.getItemsManager().removeItem(items.get(requirement.getTier()));
                                }
                                for (Map.Entry<MobDrops, Long> currenciesLongEntry : tierCostInfo.getCost().entrySet()) {
                                    currenciesLongEntry.getKey().subtractFromPlayer(databasePlayer, currenciesLongEntry.getValue());
                                }

                                AbstractItem<?, ?, ?> craftedItem = ItemType.getRandom().create.apply(tier);
                                pveStats.getItemsManager().addItem(craftedItem);
                                AbstractItem.sendItemMessage(player,
                                        new ComponentBuilder(ChatColor.GRAY + "You crafted ")
                                                .appendHoverItem(craftedItem.getName(), craftedItem.generateItemStack())
                                );
                                player.closeInventory();
                            },
                            (m2, e2) -> openForgingMenu(player, databasePlayer, tier, items),
                            (m2) -> {
                            }
                    );
                }
        );
    }

    static class TierRequirement {
        private final ItemTier tier;
        private final int x;
        private final int y;

        TierRequirement(ItemTier tier, int x, int y) {
            this.tier = tier;
            this.x = x;
            this.y = y;
        }

        public ItemTier getTier() {
            return tier;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    static class TierCostInfo {
        private final LinkedHashMap<MobDrops, Long> cost;
        private final Pair<Integer, Integer> costLocation;
        private final List<TierRequirement> requirements;

        TierCostInfo(LinkedHashMap<MobDrops, Long> cost, Pair<Integer, Integer> costLocation, List<TierRequirement> requirements) {
            this.cost = cost;
            this.costLocation = costLocation;
            this.requirements = requirements;
        }

        public LinkedHashMap<MobDrops, Long> getCost() {
            return cost;
        }

        public Pair<Integer, Integer> getCostLocation() {
            return costLocation;
        }

        public List<TierRequirement> getRequirements() {
            return requirements;
        }

    }

}
