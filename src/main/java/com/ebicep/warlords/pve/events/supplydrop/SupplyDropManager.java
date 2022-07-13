package com.ebicep.warlords.pve.events.supplydrop;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.java.NumberFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SupplyDropManager {

    private static HashMap<UUID, Boolean> playerRollCooldown = new HashMap<>();

    public static void sendSupplyDropMessage(UUID uuid, String message) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (offlinePlayer != null && offlinePlayer.isOnline()) {
            offlinePlayer.getPlayer().sendMessage(ChatColor.GOLD + "Supply Drop" + ChatColor.DARK_GRAY + " > " + message);
        }
    }

    public static void openSupplyDropMenu(Player player) {
        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
        if (databasePlayer == null) {
            player.sendMessage(ChatColor.RED + "Susan does not want to talk to you right now. fucking loser");
            return;
        }
        DatabasePlayerPvE databasePlayerPvE = databasePlayer.getPveStats();

        Menu menu = new Menu("Supply Drop", 9 * 6);

        menu.setItem(
                4,
                1,
                new ItemBuilder(Material.GOLD_NUGGET)
                        .name(ChatColor.GREEN + "Click to buy a supply drop token")
                        .lore(
                                ChatColor.GREEN + "Cost: " + ChatColor.YELLOW + "10,000 coins",
                                ChatColor.GREEN + "Balance: " + ChatColor.YELLOW + NumberFormat.addCommas(databasePlayerPvE.getCoins()) + " coins")
                        .get(),
                (m, e) -> {
                    if (databasePlayerPvE.getCoins() < 10000) {
                        player.sendMessage(ChatColor.RED + "You do not have enough coins to buy a supply drop token!");
                        player.closeInventory();
                        return;
                    }
                    databasePlayerPvE.addCoins(-10000);
                    databasePlayerPvE.addSupplyDropRoll(1);

                });

        menu.setItem(
                2,
                3,
                new ItemBuilder(Material.GOLD_BARDING)
                        .name(ChatColor.GREEN + "Click to call a supply drop")
                        .lore(
                                ChatColor.GRAY + "Cost: " + ChatColor.YELLOW + "1 Token",
                                ChatColor.GRAY + "Balance: " + ChatColor.YELLOW + NumberFormat.addCommas(databasePlayerPvE.getSupplyDropTokens()) + " Token" + (databasePlayerPvE.getSupplyDropTokens() > 1 ? "s" : ""))
                        .get(),
                (m, e) -> {
                    if (playerRollCooldown.getOrDefault(player.getUniqueId(), false)) {
                        player.sendMessage(ChatColor.RED + "You must wait for your current roll to end to roll again!");
                        return;
                    }
                    if (databasePlayerPvE.getSupplyDropTokens() > 0) {
                        supplyDropRoll(player, 1);
                    } else {
                        player.sendMessage(ChatColor.RED + "You do not have any supply drop tokens to call a supply drop.");
                    }
                    player.closeInventory();
                });
        menu.setItem(
                6,
                3,
                new ItemBuilder(Material.DIAMOND_BARDING)
                        .name(ChatColor.GREEN + "Click to call all available supply drops")
                        .lore(
                                ChatColor.GRAY + "Cost: " + ChatColor.YELLOW + NumberFormat.addCommas(databasePlayerPvE.getSupplyDropTokens()) + " Token" + (databasePlayerPvE.getSupplyDropTokens() > 1 ? "s" : ""),
                                ChatColor.GRAY + "Balance: " + ChatColor.YELLOW + NumberFormat.addCommas(databasePlayerPvE.getSupplyDropTokens()) + " Token" + (databasePlayerPvE.getSupplyDropTokens() > 1 ? "s" : ""))
                        .get(),
                (m, e) -> {
                    if (playerRollCooldown.getOrDefault(player.getUniqueId(), false)) {
                        player.sendMessage(ChatColor.RED + "You must wait for your current roll to end to roll again!");
                        return;
                    }
                    if (databasePlayerPvE.getSupplyDropTokens() > 0) {
                        supplyDropRoll(player, databasePlayerPvE.getSupplyDropTokens());
                    } else {
                        player.sendMessage(ChatColor.RED + "You do not have any supply drop tokens to call a supply drop.");
                    }
                    player.closeInventory();
                });

        //last 20 supply drops
        List<String> supplyDropHistory = databasePlayerPvE.getSupplyDropEntries()
                .subList(Math.max(0, databasePlayerPvE.getSupplyDropEntries().size() - 20), databasePlayerPvE.getSupplyDropEntries().size())
                .stream()
                .map(SupplyDropEntry::getReward)
                .map(supplyDropRewards -> supplyDropRewards.getChatColor() + supplyDropRewards.name + "\n")
                .collect(Collectors.toList());
        menu.setItem(
                5,
                5,
                new ItemBuilder(Material.BOOK)
                        .name(ChatColor.GREEN + "Your most recent supply drops")
                        .lore(IntStream.range(0, supplyDropHistory.size())
                                .mapToObj(index -> ChatColor.GRAY.toString() + (index + 1) + ". " + supplyDropHistory.get(supplyDropHistory.size() - index - 1))
                                .collect(Collectors.toList()))
                        .get(),
                (m, e) -> {

                });

        menu.setItem(4, 5, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void supplyDropRoll(Player player, int amount) {
        playerRollCooldown.put(player.getUniqueId(), true);
        sendSupplyDropMessage(player.getUniqueId(), ChatColor.GREEN + "Called " + ChatColor.YELLOW + amount + ChatColor.GREEN + " supply drop" + (amount > 1 ? "s" : "") + "!");

        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
        DatabasePlayerPvE databasePlayerPvE = databasePlayer.getPveStats();
        databasePlayerPvE.setSupplyDropTokens(databasePlayerPvE.getSupplyDropTokens() - amount);

        int rolls = amount == 1 ? 1 : 10;
        int slownessIncrementRate = amount == 1 ? 20 : 9;
        int slownessMax = amount == 1 ? 9 : 5;

        new BukkitRunnable() {

            int counter = 0;
            int slowness = 1;
            int rewardsGained = 0;
            int cooldown = 0;
            SupplyDropRewards reward;

            @Override
            public void run() {
                if (cooldown > 0) {
                    cooldown--;
                    return;
                }

                if (counter % slowness == 0) {
                    reward = SupplyDropRewards.getRandomReward();
                    PacketUtils.sendTitle(
                            player.getUniqueId(),
                            reward.getChatColor() + reward.name,
                            "",
                            0, 100, 0
                    );
                }

                counter++;

                if (counter % slownessIncrementRate == 0) {
                    slowness++;
                    if (slowness == slownessMax) {
                        reward.givePlayerRewardTitle(player);
                        slowness = 1;
                        rewardsGained++;
                        cooldown = 13;
                        sendSupplyDropMessage(player.getUniqueId(), ChatColor.GREEN + "Won: " + reward.getChatColor() + reward.name + "");
                        databasePlayerPvE.addSupplyDropEntry(new SupplyDropEntry(reward));
                        if (rewardsGained == rolls) {
                            playerRollCooldown.put(player.getUniqueId(), false);
                            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                            cancel();
                        }
                    }
                }
            }
        }.runTaskTimer(Warlords.getInstance(), 0, 0);

    }

}
