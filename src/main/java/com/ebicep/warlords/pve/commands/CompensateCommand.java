package com.ebicep.warlords.pve.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardManager;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.util.bukkit.Colors;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.signgui.SignGUI;
import com.ebicep.warlords.util.chat.ChatChannels;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ebicep.warlords.menu.Menu.ACTION_CLOSE_MENU;
import static com.ebicep.warlords.menu.Menu.MENU_CLOSE;

@CommandAlias("compensate")
@CommandPermission("group.administrator")
public class CompensateCommand extends BaseCommand {

    public static void openCompensateMenu(Player player, LinkedHashMap<Currencies, Long> compensation, List<DatabasePlayer> compensatedPlayers) {
        Menu menu = new Menu("Compensate", 9 * 6);

        int row = 1;
        int col = 1;
        for (Currencies currency : Currencies.VALUES) {
            menu.setItem(col, row,
                    new ItemBuilder(currency.item)
                            .name(currency.getColoredName())
                            .lore(ChatColor.GREEN.toString() + compensation.getOrDefault(currency, 0L))
                            .flags(ItemFlag.HIDE_POTION_EFFECTS)
                            .get(),
                    (m, e) -> {
                        String[] text = new String[]{"", "", "", ""};
                        String[] currencyNameSplit = currency.name.split(" ");
                        System.arraycopy(currencyNameSplit, 0, text, 1, currencyNameSplit.length);
                        SignGUI.open(player, text, (p, lines) -> {
                            String amount = lines[0];
                            try {
                                int amountInt = Integer.parseInt(amount);
                                compensation.put(currency, (long) amountInt);
                            } catch (Exception exception) {
                                p.sendMessage(ChatColor.RED + "Invalid Amount");
                            }
                            openCompensateMenu(p, compensation, compensatedPlayers);
                        });
                    }
            );

            if (++col >= 8) {
                col = 1;
                row++;
            }
        }

        Set<DatabasePlayer> databasePlayers = StatsLeaderboardManager.CACHED_PLAYERS.get(PlayersCollections.LIFETIME);
        menu.setItem(3, 5,
                new ItemBuilder(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal())
                        .name(ChatColor.GREEN + "Player")
                        .lore(ChatColor.AQUA + (compensatedPlayers == null ? "Not Selected" :
                                compensatedPlayers.size() == 1 ? compensatedPlayers.get(0).getName() : "All " + databasePlayers.size() + " Players"))
                        .get(),
                (m, e) -> {
                    SignGUI.open(player, new String[]{"", "Enter", "Player", "Name"}, (p, lines) -> {
                        String playerName = lines[0];
                        for (DatabasePlayer databasePlayer : databasePlayers) {
                            if (databasePlayer.getName().equalsIgnoreCase(playerName)) {
                                openCompensateMenu(player, compensation, List.of(databasePlayer));
                                return;
                            }
                        }
                        ChatChannels.sendDebugMessage(player,
                                ChatColor.AQUA + playerName + ChatColor.RED + " was not found for compensation",
                                true
                        );
                    });
                }
        );
        menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.setItem(5, 5,
                new ItemBuilder(Material.CHEST)
                        .name(ChatColor.GREEN + "All Players")
                        .get(),
                (m, e) -> {
                    openCompensateMenu(player, compensation, new ArrayList<>(databasePlayers));
                }
        );
        if (compensatedPlayers != null) {
            menu.setItem(8, 5,
                    new ItemBuilder(Colors.GREEN.wool)
                            .name(ChatColor.GREEN + "Confirm Compensate")
                            .lore(compensation.entrySet()
                                    .stream()
                                    .map(currenciesValues -> ChatColor.GRAY + " - " + currenciesValues.getKey().getCostColoredName(currenciesValues.getValue()))
                                    .toArray(String[]::new))
                            .get(),
                    (m, e) -> {
                        System.out.println(compensation);
                        System.out.println(compensatedPlayers);
                        if (compensatedPlayers.size() == 1) {
                            Warlords.newChain()
                                    .sync(() -> {
                                        DatabasePlayer databasePlayer = compensatedPlayers.get(0);
                                        DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
                                        compensation.forEach(pveStats::addCurrency);
                                        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                                        ChatChannels.playerSpigotSendMessage(player, ChatChannels.DEBUG,
                                                new ComponentBuilder()
                                                        .appendHoverText(
                                                                ChatColor.GREEN + "Compensated " + ChatColor.AQUA + databasePlayer.getName(),
                                                                compensation.entrySet()
                                                                        .stream()
                                                                        .map(currenciesValues -> ChatColor.GRAY + " - " + currenciesValues.getKey()
                                                                                .getCostColoredName(currenciesValues.getValue()))
                                                                        .collect(Collectors.joining("\n"))
                                                        )
                                        );
                                        player.closeInventory();
                                    }).execute();

                        } else {
                            Warlords.newChain()
                                    .sync(() -> {
                                        for (DatabasePlayer databasePlayer : compensatedPlayers) {
                                            DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
                                            compensation.forEach(pveStats::addCurrency);
                                            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                                        }
                                        ChatChannels.sendDebugMessage(player,
                                                ChatColor.GREEN + "All " + ChatColor.AQUA + compensatedPlayers.size() + " players " +
                                                        ChatColor.GREEN + "were given compensation",
                                                true
                                        );
                                        player.closeInventory();
                                    })
                                    .execute();
                        }
                    }
            );
        }
        menu.openForPlayer(player);
    }

    @Default
    public void openCompensateMenu(Player player) {
        openCompensateMenu(player, new LinkedHashMap<>(), null);
    }


//    @Default
//    public void compensate(Player player, DatabasePlayerFuture databasePlayerFuture, Integer coins, Integer shards) {
//        databasePlayerFuture.getFuture().thenAccept(databasePlayer -> {
//            player.spigot().sendMessage(
//                    new ComponentBuilder(ChatColor.GREEN + "Give ")
//                            .appendHoverText(ChatColor.GOLD + "compensation",
//                                    Currencies.COIN.getCostColoredName(coins) + "\n" +
//                                            Currencies.SYNTHETIC_SHARD.getCostColoredName(shards)
//                            )
//                            .append(ChatColor.GREEN + " to " + ChatColor.AQUA + databasePlayer.getName())
//                            .append(ChatColor.GREEN + " [CONFIRM]")
//                            .appendClickEvent(ClickEvent.Action.RUN_COMMAND, "/compensate confirm " + databasePlayer.getName() + " " + coins + " " + shards)
//                            .create()
//            );
//        });
//    }
//
//    @Private
//    @Subcommand("confirm")
//    public void compensateConfirm(Player player, DatabasePlayerFuture databasePlayerFuture, Integer coins, Integer shards) {
//        databasePlayerFuture.getFuture().thenAccept(databasePlayer -> {
//            DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
//            pveStats.addCurrency(Currencies.COIN, coins);
//            pveStats.addCurrency(Currencies.SYNTHETIC_SHARD, shards);
//            player.spigot().sendMessage(
//                    new ComponentBuilder(ChatColor.GREEN + "Gave ")
//                            .appendHoverText(ChatColor.GOLD + "compensation", "Coins: " + coins + "\nShards: " + shards)
//                            .append(ChatColor.GREEN + " to " + ChatColor.AQUA + databasePlayer.getName())
//                            .create()
//            );
//        });
//    }

}
