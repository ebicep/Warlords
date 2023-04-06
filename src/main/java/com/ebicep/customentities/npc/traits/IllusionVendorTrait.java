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
import com.ebicep.warlords.util.java.DateUtil;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.trait.HologramTrait;
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
            new SpendableBuyShop(1, MobDrops.ZENITH_STAR, 3, 30),
            new SpendableBuyShop(200, Currencies.LEGEND_FRAGMENTS, 1, 20),
            new SpendableBuyShop(1, Currencies.CELESTIAL_BRONZE, 1, 500),
            new SpendableBuyShop(50, Currencies.FAIRY_ESSENCE, 1, 10),
            new SpendableBuyShop(1, Currencies.RARE_STAR_PIECE, 1, 10)
    );

    public static void openIllusionVendor(Player player, DatabasePlayer databasePlayer, DatabasePlayer databasePlayerWeekly) {
        Menu menu = new Menu("Illusion Vendor", 9 * 4);

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
            Long purchasedAmount = weeklyRewardsPurchased.getOrDefault(mapName, 0L);
            if (rewardSpendable == MobDrops.ZENITH_STAR) {
                rewardPrice += purchasedAmount * 10;
            }

            String stock;
            if (reward.getStock() == -1) {
                stock = "Unlimited";
            } else {
                stock = "" + (reward.getStock() - purchasedAmount);
            }
            int finalRewardPrice = rewardPrice;
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
                        if (pveStats.getCurrencyValue(Currencies.ILLUSION_SHARD) < finalRewardPrice) {
                            player.sendMessage(ChatColor.RED + "You need " + Currencies.ILLUSION_SHARD.getCostColoredName(finalRewardPrice) + ChatColor.RED + " to purchase this item!");
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 2, 0.5f);
                            return;
                        }
                        if (reward.getStock() != -1 && purchasedAmount >= reward.getStock()) {
                            player.sendMessage(ChatColor.RED + "This item is out of stock!");
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 2, 0.5f);
                            return;
                        }
                        pveStats.subtractCurrency(Currencies.ILLUSION_SHARD, finalRewardPrice);
                        rewardSpendable.addToPlayer(databasePlayer, rewardAmount);

                        pveStats.getIllusionVendorRewardsPurchased().merge(mapName, 1L, Long::sum);
                        weeklyRewardsPurchased.merge(mapName, 1L, Long::sum);

                        player.sendMessage(ChatColor.GREEN + "Purchased " + rewardSpendable.getCostColoredName(rewardAmount) +
                                ChatColor.GREEN + " for " + Currencies.ILLUSION_SHARD.getCostColoredName(finalRewardPrice) +
                                ChatColor.GREEN + "!");
                        player.playSound(player.getLocation(), Sound.LEVEL_UP, 500, 2f);
                        openIllusionVendor(player, databasePlayer, databasePlayerWeekly);

                        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                        DatabaseManager.queueUpdatePlayerAsync(databasePlayerWeekly, PlayersCollections.WEEKLY);
                    }
            );
        }

        menu.setItem(4, 3, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.addBorder(Menu.GRAY_EMPTY_PANE, true);
        menu.openForPlayer(player);
    }

    private HologramTrait hologramTrait = null;

    public IllusionVendorTrait() {
        super("IllusionVendorTrait");
    }

    @Override
    public void onSpawn() {
        HologramTrait hologramTrait = npc.getOrAddTrait(HologramTrait.class);
        hologramTrait.setLine(0, ChatColor.YELLOW.toString() + ChatColor.BOLD + "RIGHT-CLICK");
        hologramTrait.setLine(1, ChatColor.GREEN + "Illusion Vendor");
        this.hologramTrait = hologramTrait;
    }

    @Override
    public void run() {
        if (hologramTrait == null) {
            return;
        }
        String timeTill = DateUtil.getTimeTill(DateUtil.getResetDateLatestMonday(),
                true,
                true,
                true,
                false
        );
        if (timeTill.equals("0 seconds")) {
            hologramTrait.setLine(2, ChatColor.GOLD.toString() + ChatColor.BOLD + "Shipment Arriving Soon!");
        } else {
            hologramTrait.setLine(2, ChatColor.GOLD.toString() + ChatColor.BOLD + "Next Shipment in " + timeTill);
        }
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

}
