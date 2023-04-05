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
import com.ebicep.warlords.pve.mobs.MobDrops;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class IllusionVendorTrait extends WarlordsTrait {

    private static final List<SpendableBuyShop> SHOP = List.of(
            new SpendableBuyShop(1, MobDrops.ZENITH_STAR, 1, 30),
            new SpendableBuyShop(200, Currencies.LEGEND_FRAGMENTS, 1, 20),
            new SpendableBuyShop(1, Currencies.CELESTIAL_BRONZE, 1, 500),
            new SpendableBuyShop(50, Currencies.FAIRY_ESSENCE, 1, 10),
            new SpendableBuyShop(1, Currencies.RARE_STAR_PIECE, 1, 10)
    );

    public IllusionVendorTrait() {
        super("IllusionVendorTrait");
    }

    @Override
    public void rightClick(NPCRightClickEvent event) {
        Player player = event.getClicker();
        UUID uuid = player.getUniqueId();
        DatabaseManager.getPlayer(uuid, databasePlayer -> {
            DatabaseManager.getPlayer(uuid, PlayersCollections.WEEKLY, databasePlayerWeekly -> {
                openIllusionVendor(player, databasePlayer, databasePlayerWeekly);
            });
        });
    }

    public static void openIllusionVendor(Player player, DatabasePlayer databasePlayer, DatabasePlayer databasePlayerWeekly) {
        Menu menu = new Menu("Illusion Vendor", 9 * 4);

        for (int i = 0; i < 9; i++) {
            menu.setItem(i, 0,
                    new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 7)
                            .name(" ")
                            .get(),
                    (m, e) -> {
                    }
            );
        }
        for (int i = 0; i < 9; i++) {
            menu.setItem(i, 3,
                    new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 7)
                            .name(" ")
                            .get(),
                    (m, e) -> {
                    }
            );
        }
        for (int i = 1; i < 3; i++) {
            menu.setItem(0, i,
                    new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 7)
                            .name(" ")
                            .get(),
                    (m, e) -> {
                    }
            );
        }
        for (int i = 1; i < 3; i++) {
            menu.setItem(8, i,
                    new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 7)
                            .name(" ")
                            .get(),
                    (m, e) -> {
                    }
            );
        }


        DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
        DatabasePlayerPvE weeklyPveStats = databasePlayerWeekly.getPveStats();
        Map<String, Long> weeklyRewardsPurchased = weeklyPveStats.getIllusionVendorRewardsPurchased();

        menu.setItem(4, 0,
                new ItemBuilder(Material.CHEST)
                        .name(Currencies.ILLUSION_SHARD.getCostColoredName(pveStats.getCurrencyValue(Currencies.ILLUSION_SHARD)))
                        .get(),
                (m, e) -> {

                }
        );
        for (int i = 0; i < SHOP.size(); i++) {
            SpendableBuyShop reward = SHOP.get(i);
            int rewardAmount = reward.getAmount();
            Spendable rewardSpendable = reward.getSpendable();
            int rewardPrice = reward.getPrice();
            String mapName = reward.getMapName();

            String stock;
            if (reward.getStock() == -1) {
                stock = "Unlimited";
            } else {
                stock = "" + (reward.getStock() - weeklyRewardsPurchased.getOrDefault(mapName, 0L));
            }
            menu.setItem(i + 1, 1,
                    new ItemBuilder(rewardSpendable.getItem())
                            .name(rewardSpendable.getCostColoredName(rewardAmount))
                            .lore(
                                    ChatColor.GRAY + "Cost: " + ChatColor.YELLOW + Currencies.ILLUSION_SHARD.getCostColoredName(rewardPrice),
                                    ChatColor.GRAY + "Stock: " + ChatColor.YELLOW + stock
                            )
                            .flags(ItemFlag.HIDE_POTION_EFFECTS)
                            .get(),
                    (m, e) -> {
                        if (pveStats.getCurrencyValue(Currencies.ILLUSION_SHARD) < rewardPrice) {
                            player.sendMessage(ChatColor.RED + "You need " + Currencies.ILLUSION_SHARD.getCostColoredName(rewardPrice) + ChatColor.RED + " to purchase this item!");
                            return;
                        }
                        if (reward.getStock() != -1 && weeklyRewardsPurchased.getOrDefault(mapName, 0L) >= reward.getStock()) {
                            player.sendMessage(ChatColor.RED + "This item is out of stock!");
                            return;
                        }
                        pveStats.subtractCurrency(Currencies.ILLUSION_SHARD, rewardPrice);
                        rewardSpendable.addToPlayer(databasePlayer, rewardAmount);

                        pveStats.getIllusionVendorRewardsPurchased().merge(mapName, 1L, Long::sum);
                        weeklyRewardsPurchased.merge(mapName, 1L, Long::sum);

                        player.sendMessage(ChatColor.GREEN + "Purchased " + rewardSpendable.getCostColoredName(rewardAmount) +
                                ChatColor.GREEN + " for " + Currencies.ILLUSION_SHARD.getCostColoredName(rewardPrice) +
                                ChatColor.GREEN + "!");
                        player.playSound(player.getLocation(), Sound.LEVEL_UP, 500, 2.5f);
                        openIllusionVendor(player, databasePlayer, databasePlayerWeekly);

                        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                        DatabaseManager.queueUpdatePlayerAsync(databasePlayerWeekly, PlayersCollections.WEEKLY);
                    }
            );
        }

        menu.setItem(4, 3, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

}
