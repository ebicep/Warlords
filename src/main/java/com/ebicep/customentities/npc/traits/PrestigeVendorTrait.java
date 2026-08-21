package com.ebicep.customentities.npc.traits;

import com.ebicep.customentities.npc.WarlordsTrait;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.honorifics.HonorificManager;
import com.ebicep.warlords.honorifics.HonorificMenu;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.SpendableBuyShop;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.trait.HologramTrait;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PrestigeVendorTrait extends WarlordsTrait {

    private static final List<SpendableBuyShop> SHOP = List.of(
            new SpendableBuyShop(8, Currencies.ETHEREUM_CRYSTAL, -1, 1),
            new SpendableBuyShop(4, Currencies.ASCENDANT_SHARD, -1, 1),
            new SpendableBuyShop(1, Currencies.VEILKEEPER_INSIGNIA, -1, 6)
    );

    public static void openPrestigeVendor(Player player, DatabasePlayer databasePlayer, DatabasePlayer databasePlayerWeekly) {
        Menu menu = new Menu("The Artificer", 9 * 4);
        DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
        DatabasePlayerPvE weeklyPveStats = databasePlayerWeekly.getPveStats();
        Map<String, Long> weeklyRewardsPurchased = weeklyPveStats.getIllusionVendorRewardsPurchased();

        menu.setItem(4, 0, new ItemBuilder(Material.HEART_OF_THE_SEA)
                .name(Currencies.PRESTIGE_ORB.getCostColoredName(pveStats.getCurrencyValue(Currencies.PRESTIGE_ORB)))
                .get(), Menu.ACTION_DO_NOTHING);

        for (int i = 0; i < SHOP.size(); i++) {
            SpendableBuyShop reward = SHOP.get(i);
            int rewardAmount = reward.amount();
            Spendable rewardSpendable = reward.spendable();
            int rewardPrice = reward.price();
            String mapName = reward.getMapName();
            long purchasedAmount = weeklyRewardsPurchased.getOrDefault(mapName, 0L);
            String stock = reward.stock() == -1 ? "Unlimited" : String.valueOf(reward.stock() - purchasedAmount);
            menu.setItem(i + 1, 1, new ItemBuilder(rewardSpendable.getItem())
                    .name(rewardSpendable.getCostColoredName(rewardAmount))
                    .lore(Component.text("Cost: ", NamedTextColor.GRAY).append(Currencies.PRESTIGE_ORB.getCostColoredName(rewardPrice)),
                            Component.text("Stock: ", NamedTextColor.GRAY).append(Component.text(stock, NamedTextColor.YELLOW)),
                            Component.empty(),
                            Component.text("Click to purchase", NamedTextColor.GREEN))
                    .get(), (m, e) -> openPurchaseConfirmation(player, databasePlayer, databasePlayerWeekly, reward));
        }

        menu.setItem(7, 1, new ItemBuilder(Material.NAME_TAG)
                .name(Component.text("Honorifics", NamedTextColor.GOLD))
                .lore(Component.text("View, purchase, and equip name titles.", NamedTextColor.GRAY),
                        Component.text("Customize unlocked colors and fonts.", NamedTextColor.GRAY),
                        Component.empty(), Component.text("Click to open", NamedTextColor.YELLOW))
                .get(), (m, e) -> HonorificMenu.open(player));
        menu.setItem(4, 3, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.addBorder(Menu.GRAY_EMPTY_PANE, true);
        menu.openForPlayer(player);
    }

    private static void openPurchaseConfirmation(
            Player player,
            DatabasePlayer databasePlayer,
            DatabasePlayer databasePlayerWeekly,
            SpendableBuyShop reward
    ) {
        if (!validatePurchase(player, databasePlayer, databasePlayerWeekly, reward)) {
            return;
        }

        int rewardAmount = reward.amount();
        Spendable rewardSpendable = reward.spendable();
        int rewardPrice = reward.price();
        List<Component> confirmLore = List.of(
                Component.text("Purchase ", NamedTextColor.GRAY).append(rewardSpendable.getCostColoredName(rewardAmount)),
                Component.text("Cost: ", NamedTextColor.GRAY).append(Currencies.PRESTIGE_ORB.getCostColoredName(rewardPrice))
        );

        Menu.openConfirmationMenu(
                player,
                "Confirm Artificer Purchase",
                3,
                confirmLore,
                Menu.GO_BACK,
                (m, e) -> purchase(player, databasePlayer, databasePlayerWeekly, reward),
                (m, e) -> openPrestigeVendor(player, databasePlayer, databasePlayerWeekly),
                menu -> {
                    menu.setItem(4, 1,
                            new ItemBuilder(rewardSpendable.getItem())
                                    .name(rewardSpendable.getCostColoredName(rewardAmount))
                                    .lore(Component.text("Cost: ", NamedTextColor.GRAY)
                                            .append(Currencies.PRESTIGE_ORB.getCostColoredName(rewardPrice)))
                                    .get(),
                            Menu.ACTION_DO_NOTHING
                    );
                    menu.addBorder(Menu.GRAY_EMPTY_PANE, true);
                }
        );
    }

    private static boolean validatePurchase(
            Player player,
            DatabasePlayer databasePlayer,
            DatabasePlayer databasePlayerWeekly,
            SpendableBuyShop reward
    ) {
        DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
        int rewardPrice = reward.price();
        if (pveStats.getCurrencyValue(Currencies.PRESTIGE_ORB) < rewardPrice) {
            player.sendMessage(Component.text("You need ", NamedTextColor.RED)
                    .append(Currencies.PRESTIGE_ORB.getCostColoredName(rewardPrice))
                    .append(Component.text(" to purchase this item!")));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 2, 0.5f);
            return false;
        }

        long purchasedAmount = databasePlayerWeekly.getPveStats()
                .getIllusionVendorRewardsPurchased()
                .getOrDefault(reward.getMapName(), 0L);
        if (reward.stock() != -1 && purchasedAmount >= reward.stock()) {
            player.sendMessage(Component.text("This item is out of stock!", NamedTextColor.RED));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 2, 0.5f);
            return false;
        }
        return true;
    }

    private static void purchase(
            Player player,
            DatabasePlayer databasePlayer,
            DatabasePlayer databasePlayerWeekly,
            SpendableBuyShop reward
    ) {
        if (!validatePurchase(player, databasePlayer, databasePlayerWeekly, reward)) {
            openPrestigeVendor(player, databasePlayer, databasePlayerWeekly);
            return;
        }

        DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
        int rewardAmount = reward.amount();
        Spendable rewardSpendable = reward.spendable();
        int rewardPrice = reward.price();

        pveStats.subtractCurrency(Currencies.PRESTIGE_ORB, rewardPrice);
        rewardSpendable.addToPlayer(databasePlayer, rewardAmount);
        player.sendMessage(Component.text("Purchased ", NamedTextColor.GREEN)
                .append(rewardSpendable.getCostColoredName(rewardAmount))
                .append(Component.text(" for "))
                .append(Currencies.PRESTIGE_ORB.getCostColoredName(rewardPrice))
                .append(Component.text("!")));
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 500, 2f);
        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
        DatabaseManager.queueUpdatePlayerAsync(databasePlayerWeekly, PlayersCollections.WEEKLY);
        openPrestigeVendor(player, databasePlayer, databasePlayerWeekly);
    }

    @Override
    public void onAttach() {
        HologramTrait hologramTrait = npc.getOrAddTrait(HologramTrait.class);
        hologramTrait.setLine(0, ChatColor.RED + "The Artificer");
    }

    @Override
    public void rightClick(NPCRightClickEvent event) {
        Player player = event.getClicker();
        UUID uuid = player.getUniqueId();
        openPrestigeVendor(player, DatabaseManager.getPlayer(uuid), DatabaseManager.getPlayer(uuid, PlayersCollections.WEEKLY));
    }

    public PrestigeVendorTrait() {
        super("PrestigeVendorTrait");
        HonorificManager.init();
    }
}
