package com.ebicep.customentities.npc.traits;

import com.ebicep.customentities.npc.WarlordsTrait;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.SpendableBuyShop;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.MobDrop;
import com.ebicep.warlords.pve.weapons.menu.WeaponCraftMenu;
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

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TreasureHuntVendorTrait extends WarlordsTrait {

    private static final List<SpendableBuyShop> SHOP = List.of(
            new SpendableBuyShop(1, Currencies.VOID_STAR_PIECE, 1, 500),
            new SpendableBuyShop(1, MobDrop.AWAKENED_ABILITY_SCROLL, 1, 200),
            new SpendableBuyShop(1, Currencies.ITEM_LOCK_SCROLL, 1, 200),
            new SpendableBuyShop(1, Currencies.LEGENDARY_STAR_PIECE, 1, 200),
            new SpendableBuyShop(1, MobDrop.ZENITH_STAR, 1, 100),
            new SpendableBuyShop(60, Currencies.LEGEND_FRAGMENTS, -1, 10),
            new SpendableBuyShop(100, Currencies.SYNTHETIC_SHARD, -1, 10)
    );

    public static void openTreasureHuntVendor(Player player, DatabasePlayer databasePlayer, DatabasePlayer databasePlayerWeekly) {
        Menu menu = new Menu("Archimedes", 9 * 6);

        DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
        DatabasePlayerPvE weeklyPveStats = databasePlayerWeekly.getPveStats();
        Map<String, Long> weeklyRewardsPurchased = weeklyPveStats.getIllusionVendorRewardsPurchased();

        menu.setItem(4, 0,
                new ItemBuilder(Material.CALIBRATED_SCULK_SENSOR)
                        .name(Currencies.ARCHEMEDIAN_FRAGMENT.getCostColoredName(pveStats.getCurrencyValue(Currencies.ARCHEMEDIAN_FRAGMENT)))
                        .get(),
                (m, e) -> {

                }
        );
        for (int i = 0; i < SHOP.size(); i++) {
            SpendableBuyShop reward = SHOP.get(i);
            int rewardAmount = reward.amount();
            Spendable rewardSpendable = reward.spendable();
            int rewardPrice = reward.price();
            String mapName = reward.getMapName();
            Long purchasedAmount = weeklyRewardsPurchased.getOrDefault(mapName, 0L);

            String stock;
            if (reward.stock() == -1) {
                stock = "Unlimited";
            } else {
                stock = "" + (reward.stock() - purchasedAmount);
            }
            menu.setItem(i + 1, 1,
                    new ItemBuilder(rewardSpendable.getItem())
                            .name(rewardSpendable.getCostColoredName(rewardAmount))
                            .lore(
                                    Component.text("Cost: ", NamedTextColor.GRAY).append(Currencies.ARCHEMEDIAN_FRAGMENT.getCostColoredName(rewardPrice)),
                                    Component.text("Stock: ", NamedTextColor.GRAY).append(Component.text(stock, NamedTextColor.YELLOW))
                            )
                            .get(),
                    (m, e) -> {
                        if (pveStats.getCurrencyValue(Currencies.ARCHEMEDIAN_FRAGMENT) < rewardPrice) {
                            player.sendMessage(Component.text("You need ", NamedTextColor.RED)
                                    .append(Currencies.ARCHEMEDIAN_FRAGMENT.getCostColoredName(rewardPrice))
                                    .append(Component.text(" to purchase this item!"))
                            );
                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 2, 0.5f);
                            return;
                        }
                        if (reward.stock() != -1 && purchasedAmount >= reward.stock()) {
                            player.sendMessage(Component.text("This item is out of stock!", NamedTextColor.RED));
                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 2, 0.5f);
                            return;
                        }
                        pveStats.subtractCurrency(Currencies.ARCHEMEDIAN_FRAGMENT, rewardPrice);
                        rewardSpendable.addToPlayer(databasePlayer, rewardAmount);

                        player.sendMessage(Component.text("Purchased ", NamedTextColor.GREEN)
                                .append(rewardSpendable.getCostColoredName(rewardAmount))
                                .append(Component.text(" for "))
                                .append(Currencies.ARCHEMEDIAN_FRAGMENT.getCostColoredName(rewardPrice))
                                .append(Component.text("!")));
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 500, 2f);
                        openTreasureHuntVendor(player, databasePlayer, databasePlayerWeekly);

                        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                        DatabaseManager.queueUpdatePlayerAsync(databasePlayerWeekly, PlayersCollections.WEEKLY);
                    }
            );
        }
        menu.setItem(4, 5, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.addBorder(Menu.GRAY_EMPTY_PANE, true);
        menu.openForPlayer(player);
    }

    public TreasureHuntVendorTrait() {
        super("TreasureHuntVendorTrait");
    }

    @Override
    public void onAttach() {
        HologramTrait hologramTrait = npc.getOrAddTrait(HologramTrait.class);
        hologramTrait.setLine(0, ChatColor.GOLD + "Archimedes");
    }

    private int ticksElapsed = 0;

    @Override
    public void run() {
//        if (ticksElapsed++ % 600 != 0) {
//            return;
//        }
//        HologramTrait hologramTrait = npc.getOrAddTrait(HologramTrait.class);
//        String timeTill = DateUtil.getTimeTill(DateUtil.getResetDateLatestMonday(),
//                true,
//                true,
//                true,
//                false
//        );
//        if (!timeTill.equals("0 seconds")) {
//            hologramTrait.setLine(1, ChatColor.RED.toString() + ChatColor.BOLD + "Next Shipment in " + timeTill);
//        }
    }

    @Override
    public void rightClick(NPCRightClickEvent event) {
        event.getClicker().sendMessage(Component.text("This shop is currently in development, check back later!", NamedTextColor.RED));
//        Player player = event.getClicker();
//        UUID uuid = player.getUniqueId();
//        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(uuid);
//        DatabasePlayer databasePlayerWeekly = DatabaseManager.getPlayer(uuid, PlayersCollections.WEEKLY);
//        openTreasureHuntVendor(player, databasePlayer, databasePlayerWeekly);
    }
}
