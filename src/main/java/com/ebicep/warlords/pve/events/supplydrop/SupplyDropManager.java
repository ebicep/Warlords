package com.ebicep.warlords.pve.events.supplydrop;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.rewards.Currencies;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.java.NumberFormat;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SupplyDropManager {

    private static final ConcurrentHashMap<UUID, Boolean> PLAYER_ROLL_COOLDOWN = new ConcurrentHashMap<>();

    public static void sendSupplyDropMessage(UUID uuid, String message) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (offlinePlayer != null && offlinePlayer.isOnline()) {
            offlinePlayer.getPlayer().sendMessage(ChatColor.GOLD + "Supply Drop" + ChatColor.DARK_GRAY + " > " + message);
        }
    }

    public static void openSupplyDropMenu(Player player) {
        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
        if (databasePlayer == null) {
            player.sendMessage(ChatColor.RED + "Susan does not want to talk to you right now.");
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
                                ChatColor.GREEN + "Balance: " + ChatColor.YELLOW + NumberFormat.addCommas(databasePlayerPvE.getCurrencyValue(Currencies.COIN)) + " coins")
                        .get(),
                (m, e) -> {
                    if (databasePlayerPvE.getCurrencyValue(Currencies.COIN) < 10000) {
                        player.sendMessage(ChatColor.RED + "You do not have enough coins to buy a supply drop token!");
                        player.closeInventory();
                        return;
                    }
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 2);
                    databasePlayerPvE.subtractCurrency(Currencies.COIN, 10000);
                    databasePlayerPvE.addCurrency(Currencies.SUPPLY_DROP_TOKEN, 1);
                    openSupplyDropMenu(player);
                });

        menu.setItem(
                2,
                3,
                new ItemBuilder(Material.GOLD_BARDING)
                        .name(ChatColor.GREEN + "Click to call a supply drop")
                        .lore(
                                ChatColor.GRAY + "Cost: " + ChatColor.YELLOW + "1 Token",
                                ChatColor.GRAY + "Balance: " + ChatColor.YELLOW + NumberFormat.addCommas(databasePlayerPvE.getCurrencyValue(Currencies.SUPPLY_DROP_TOKEN)) + " Token" + (databasePlayerPvE.getCurrencyValue(
                                        Currencies.SUPPLY_DROP_TOKEN) != 1 ? "s" : ""),
                                "",
                                ChatColor.YELLOW.toString() + ChatColor.BOLD + "SHIFT-CLICK" + ChatColor.GRAY + " to INSTANTLY call a supply drop"
                        )
                        .get(),
                (m, e) -> {
                    if (PLAYER_ROLL_COOLDOWN.getOrDefault(player.getUniqueId(), false)) {
                        player.sendMessage(ChatColor.RED + "You must wait for your current roll to end to roll again!");
                        return;
                    }
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 2);
                    if (databasePlayerPvE.getCurrencyValue(Currencies.SUPPLY_DROP_TOKEN) > 0) {
                        supplyDropRoll(player, 1, e.isShiftClick());
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
                                ChatColor.GRAY + "Cost: " + ChatColor.YELLOW + NumberFormat.addCommas(databasePlayerPvE.getCurrencyValue(Currencies.SUPPLY_DROP_TOKEN)) + " Token" + (databasePlayerPvE.getCurrencyValue(
                                        Currencies.SUPPLY_DROP_TOKEN) != 1 ? "s" : ""),
                                ChatColor.GRAY + "Balance: " + ChatColor.YELLOW + NumberFormat.addCommas(databasePlayerPvE.getCurrencyValue(Currencies.SUPPLY_DROP_TOKEN)) + " Token" + (databasePlayerPvE.getCurrencyValue(
                                        Currencies.SUPPLY_DROP_TOKEN) != 1 ? "s" : ""),
                                "",
                                ChatColor.YELLOW.toString() + ChatColor.BOLD + "SHIFT-CLICK" + ChatColor.GRAY + " to INSTANTLY call all available supply drops"
                        )
                        .get(),
                (m, e) -> {
                    if (PLAYER_ROLL_COOLDOWN.getOrDefault(player.getUniqueId(), false)) {
                        player.sendMessage(ChatColor.RED + "You must wait for your current roll to end to roll again!");
                        return;
                    }
                    if (databasePlayerPvE.getCurrencyValue(Currencies.SUPPLY_DROP_TOKEN) > 0) {
                        supplyDropRoll(player, Math.toIntExact(databasePlayerPvE.getCurrencyValue(Currencies.SUPPLY_DROP_TOKEN)), e.isShiftClick());
                    } else {
                        player.sendMessage(ChatColor.RED + "You do not have any supply drop tokens to call a supply drop.");
                    }
                    player.closeInventory();
                });

        //last 20 supply drops
        List<SupplyDropEntry> supplyDropEntries = databasePlayerPvE.getSupplyDropEntries();
        List<String> supplyDropHistory = supplyDropEntries
                .subList(Math.max(0, supplyDropEntries.size() - 20), supplyDropEntries.size())
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

    public static void supplyDropRoll(Player player, int amount, boolean instant) {
        PLAYER_ROLL_COOLDOWN.put(player.getUniqueId(), true);
        sendSupplyDropMessage(player.getUniqueId(), ChatColor.GREEN + "Called " + ChatColor.YELLOW + amount + ChatColor.GREEN + " supply drop" + (amount > 1 ? "s" : "") + "!");

        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
        DatabasePlayerPvE databasePlayerPvE = databasePlayer.getPveStats();
        databasePlayerPvE.subtractCurrency(Currencies.SUPPLY_DROP_TOKEN, amount);

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

                if (instant || counter % slowness == 0) {
                    reward = SupplyDropRewards.getRandomReward();
                    Random random = new Random();
                    player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1, random.nextFloat());
                    PacketUtils.sendTitle(
                            player.getUniqueId(),
                            reward.getChatColor() + reward.name,
                            "",
                            0, 100, 0
                    );
                }

                counter++;

                if (instant || counter % slownessIncrementRate == 0) {
                    slowness++;
                    if (instant || slowness == slownessMax) {
                        reward.givePlayerRewardTitle(player);
                        slowness = 1;
                        rewardsGained++;
                        cooldown = instant ? 3 : 13;
                        sendSupplyDropMessage(player.getUniqueId(), reward.getDropMessage());
                        databasePlayerPvE.addSupplyDropEntry(new SupplyDropEntry(reward));
                        if (rewardsGained == amount) {
                            PLAYER_ROLL_COOLDOWN.put(player.getUniqueId(), false);
                            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                            cancel();
                        }
                    }
                }
            }
        }.runTaskTimer(Warlords.getInstance(), 0, 0);

    }

}
