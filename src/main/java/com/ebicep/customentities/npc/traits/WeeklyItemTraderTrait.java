package com.ebicep.customentities.npc.traits;

import com.ebicep.customentities.npc.WarlordsTrait;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.PvEUtils;
import com.ebicep.warlords.pve.newitems.EchelonTraderShop;
import com.ebicep.warlords.pve.newitems.NewItem;
import com.ebicep.warlords.pve.newitems.NewItemsUtils;
import com.ebicep.warlords.pve.newitems.tiers.NewItemTier;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.java.DateUtil;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.trait.HologramTrait;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WeeklyItemTraderTrait extends WarlordsTrait {

    private static final int WEEKLY_TIER_PURCHASE_LIMIT = 2;
    private static final String PURCHASE_KEY_PREFIX = "ECHELON_TRADER_";
    private static final int[] ITEM_POSITIONS = {2, 4, 6};
    private static final Map<Currencies, Long> SOVEREIGN_COST = createCost(
            1_000_000,
            10,
            3,
            5_000
    );
    private static final Map<Currencies, Long> LEGENDARY_COST = createCost(
            2_500_000,
            25,
            5,
            10_000
    );

    private int ticksElapsed;

    public WeeklyItemTraderTrait() {
        super("WeeklyItemTraderTrait");
    }

    private static Map<Currencies, Long> createCost(long coins, long ascendantShards, long ethereumCrystals, long legendFragments) {
        Map<Currencies, Long> cost = new LinkedHashMap<>();
        cost.put(Currencies.COIN, coins);
        cost.put(Currencies.ASCENDANT_SHARD, ascendantShards);
        cost.put(Currencies.ETHEREUM_CRYSTAL, ethereumCrystals);
        cost.put(Currencies.LEGEND_FRAGMENTS, legendFragments);
        return Collections.unmodifiableMap(cost);
    }

    public static void openEchelonTrader(Player player, DatabasePlayer databasePlayer) {
        EchelonTraderShop shop = EchelonTraderShop.getCurrentShop();
        cleanupPurchaseHistory(databasePlayer, shop.getRotationStart());

        Menu menu = new Menu("Echelon Trader", 9 * 5);
        int sovereignPurchases = getPurchases(databasePlayer, shop.getRotationStart(), NewItemTier.SOVEREIGN);
        int legendaryPurchases = getPurchases(databasePlayer, shop.getRotationStart(), NewItemTier.LEGENDARY);

        menu.setItem(4, 0,
                new ItemBuilder(Material.CLOCK)
                        .name(Component.text("Weekly Echelon Stock", NamedTextColor.AQUA))
                        .lore(
                                Component.text("Refreshes every Friday at 6 PM Eastern.", NamedTextColor.GRAY),
                                Component.text("Sovereign purchases: ", NamedTextColor.GRAY)
                                        .append(Component.text(sovereignPurchases + "/" + WEEKLY_TIER_PURCHASE_LIMIT, NamedTextColor.YELLOW)),
                                Component.text("Legendary purchases: ", NamedTextColor.GRAY)
                                        .append(Component.text(legendaryPurchases + "/" + WEEKLY_TIER_PURCHASE_LIMIT, NamedTextColor.YELLOW))
                        )
                        .get(),
                Menu.ACTION_DO_NOTHING
        );

        addTierItems(menu, player, databasePlayer, shop, shop.getSovereignItems(), NewItemTier.SOVEREIGN, SOVEREIGN_COST, 1, sovereignPurchases);
        addTierItems(menu, player, databasePlayer, shop, shop.getLegendaryItems(), NewItemTier.LEGENDARY, LEGENDARY_COST, 2, legendaryPurchases);

        menu.setItem(4, 4, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.addBorder(Menu.GRAY_EMPTY_PANE, true);
        menu.openForPlayer(player);
    }

    private static void addTierItems(
            Menu menu,
            Player player,
            DatabasePlayer databasePlayer,
            EchelonTraderShop shop,
            List<NewItem> items,
            NewItemTier tier,
            Map<Currencies, Long> cost,
            int row,
            int purchases
    ) {
        for (int i = 0; i < items.size(); i++) {
            NewItem item = items.get(i);
            ItemBuilder itemBuilder = item.getItemBuilder()
                    .addLore(Component.empty())
                    .addLore(Component.text("Click to purchase", NamedTextColor.GREEN))
                    .addLore(PvEUtils.getCostLore(cost, "Price", false))
                    .addLore(Component.text("Tier purchases this week: ", NamedTextColor.GRAY)
                            .append(Component.text(purchases + "/" + WEEKLY_TIER_PURCHASE_LIMIT, NamedTextColor.YELLOW)));
            menu.setItem(ITEM_POSITIONS[i], row, itemBuilder.get(),
                    (m, e) -> openPurchaseConfirmation(player, databasePlayer, shop, item, tier, cost));
        }
    }

    private static void openPurchaseConfirmation(
            Player player,
            DatabasePlayer databasePlayer,
            EchelonTraderShop shop,
            NewItem item,
            NewItemTier tier,
            Map<Currencies, Long> cost
    ) {
        if (!validatePurchase(player, databasePlayer, shop, tier, cost)) {
            return;
        }

        List<Component> confirmLore = new ArrayList<>();
        confirmLore.add(Component.text("Purchase ", NamedTextColor.GRAY).append(item.getName()));
        confirmLore.addAll(PvEUtils.getCostLore(cost, "Price", true));

        Menu.openConfirmationMenu(
                player,
                "Confirm Purchase",
                3,
                confirmLore,
                Menu.GO_BACK,
                (m, e) -> purchaseItem(player, databasePlayer, shop, item, tier, cost),
                (m, e) -> openEchelonTrader(player, databasePlayer),
                menu -> {
                    menu.setItem(4, 1, item.getItemBuilder().get(), Menu.ACTION_DO_NOTHING);
                    menu.addBorder(Menu.GRAY_EMPTY_PANE, true);
                }
        );
    }

    private static void purchaseItem(
            Player player,
            DatabasePlayer databasePlayer,
            EchelonTraderShop shop,
            NewItem item,
            NewItemTier tier,
            Map<Currencies, Long> cost
    ) {
        if (!validatePurchase(player, databasePlayer, shop, tier, cost)) {
            openEchelonTrader(player, databasePlayer);
            return;
        }

        cost.forEach((spendable, amount) -> spendable.subtractFromPlayer(databasePlayer, amount));
        NewItem purchasedItem = new NewItem(item);
        databasePlayer.getPveStats().getNewItemsManager().addItem(purchasedItem);
        incrementPurchases(databasePlayer, shop.getRotationStart(), tier);
        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);

        NewItemsUtils.sendItemMessage(
                player,
                Component.text("You purchased ", NamedTextColor.GRAY)
                        .append(purchasedItem.getHoverComponent())
                        .append(Component.text(" from the Echelon Trader."))
        );
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2, 1);
        openEchelonTrader(player, databasePlayer);
    }

    private static boolean validatePurchase(
            Player player,
            DatabasePlayer databasePlayer,
            EchelonTraderShop shop,
            NewItemTier tier,
            Map<Currencies, Long> cost
    ) {
        EchelonTraderShop currentShop = EchelonTraderShop.getCurrentShop();
        if (!currentShop.getRotationStart().equals(shop.getRotationStart())) {
            player.sendMessage(Component.text("The Echelon Trader's stock has rotated. Please select an item again.", NamedTextColor.RED));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 2, 0.5f);
            return false;
        }

        cleanupPurchaseHistory(databasePlayer, shop.getRotationStart());
        if (getPurchases(databasePlayer, shop.getRotationStart(), tier) >= WEEKLY_TIER_PURCHASE_LIMIT) {
            player.sendMessage(Component.text("You have already purchased the weekly limit of " + tier.getName() + " items.", NamedTextColor.RED));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 2, 0.5f);
            return false;
        }

        for (Map.Entry<Currencies, Long> entry : cost.entrySet()) {
            if (entry.getKey().getFromPlayer(databasePlayer) < entry.getValue()) {
                player.sendMessage(Component.text("You need ", NamedTextColor.RED)
                        .append(entry.getKey().getCostColoredName(entry.getValue()))
                        .append(Component.text(" to purchase this item!")));
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 2, 0.5f);
                return false;
            }
        }
        return true;
    }

    private static void cleanupPurchaseHistory(DatabasePlayer databasePlayer, Instant rotationStart) {
        String currentPrefix = PURCHASE_KEY_PREFIX + rotationStart.getEpochSecond() + "_";
        databasePlayer.getPveStats()
                .getSeasonalVendorRewardsPurchased()
                .keySet()
                .removeIf(key -> key.startsWith(PURCHASE_KEY_PREFIX) && !key.startsWith(currentPrefix));
    }

    private static int getPurchases(DatabasePlayer databasePlayer, Instant rotationStart, NewItemTier tier) {
        return databasePlayer.getPveStats()
                .getSeasonalVendorRewardsPurchased()
                .getOrDefault(getPurchaseKey(rotationStart, tier), 0L)
                .intValue();
    }

    private static void incrementPurchases(DatabasePlayer databasePlayer, Instant rotationStart, NewItemTier tier) {
        DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
        String purchaseKey = getPurchaseKey(rotationStart, tier);
        pveStats.getSeasonalVendorRewardsPurchased().merge(purchaseKey, 1L, Long::sum);
    }

    private static String getPurchaseKey(Instant rotationStart, NewItemTier tier) {
        return PURCHASE_KEY_PREFIX + rotationStart.getEpochSecond() + "_" + tier.name();
    }

    @Override
    public void onAttach() {
        HologramTrait hologramTrait = npc.getOrAddTrait(HologramTrait.class);
        hologramTrait.setLine(0, ChatColor.AQUA + "Echelon Trader");
    }

    @Override
    public void run() {
        if (ticksElapsed++ % 600 != 0) {
            return;
        }
        EchelonTraderShop shop = EchelonTraderShop.getCurrentShop();
        String timeTill = DateUtil.getTimeTill(shop.getNextRotation(), true, true, true, false);
        HologramTrait hologramTrait = npc.getOrAddTrait(HologramTrait.class);
        hologramTrait.setLine(1, ChatColor.RED.toString() + ChatColor.BOLD + "New stock in " + timeTill);
    }

    @Override
    public void rightClick(NPCRightClickEvent event) {
        Player player = event.getClicker();
        openEchelonTrader(player, DatabaseManager.getPlayer(player));
    }
}
