package com.ebicep.warlords.pve.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.rewards.types.CompensationReward;
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

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.ebicep.warlords.menu.Menu.ACTION_CLOSE_MENU;
import static com.ebicep.warlords.menu.Menu.MENU_CLOSE;

@CommandAlias("compensate")
@CommandPermission("group.administrator")
public class CompensateCommand extends BaseCommand {

    private static final Set<UUID> LOADING = new HashSet<>();

    public static void openCompensateMenu(Player player, LinkedHashMap<Currencies, Long> compensation, List<DatabasePlayer> compensatedPlayers) {
        if (compensatedPlayers == null || compensatedPlayers.isEmpty()) {
            ChatChannels.sendDebugMessage(player, ChatColor.RED + "No players to compensate!", true);
            return;
        }
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

        menu.setItem(3, 5,
                new ItemBuilder(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal())
                        .name(ChatColor.GREEN + "Player")
                        .lore(ChatColor.AQUA + (compensatedPlayers == null ? "Not Selected" :
                                compensatedPlayers.size() == 1 ? compensatedPlayers.get(0)
                                        .getName() : "All " + compensatedPlayers.size() + " Players"))
                        .get(),
                (m, e) -> {
                    SignGUI.open(player, new String[]{"", "Enter", "Player", "Name"}, (p, lines) -> {
                        String playerName = lines[0];
                        for (DatabasePlayer databasePlayer : compensatedPlayers) {
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
                        .lore(compensatedPlayers.stream()
                                .map(databasePlayer -> ChatColor.GRAY + " - " + ChatColor.AQUA + databasePlayer.getName())
                                .collect(Collectors.joining("\n")))
                        .get(),
                (m, e) -> {
                    openCompensateMenu(player, compensation, new ArrayList<>(compensatedPlayers));
                }
        );
        menu.setItem(8, 5,
                new ItemBuilder(Colors.GREEN.wool)
                        .name(ChatColor.GREEN + "Confirm Compensate")
                        .lore(compensation.entrySet()
                                .stream()
                                .map(currenciesValues -> ChatColor.GRAY + " - " + currenciesValues.getKey()
                                        .getCostColoredName(currenciesValues.getValue()))
                                .toArray(String[]::new))
                        .addLore(
                                "",
                                ChatColor.YELLOW.toString() + ChatColor.BOLD + "CLICK " + ChatColor.GREEN + "to directly give through the Rewards Inventory",
                                ChatColor.YELLOW.toString() + ChatColor.BOLD + "SHIFT-CLICK " + ChatColor.GREEN + "to directly give rewards"
                        )
                        .get(),
                (m, e) -> {
                    if (!e.isShiftClick()) {
                        SignGUI.open(player, new String[]{"", "Enter Reward", "Title", "Blank to Cancel"}, (p, lines) -> {
                            String title = lines[0];
                            if (title.isEmpty()) {
                                ChatChannels.sendDebugMessage(player, ChatColor.RED + "Blank title, compensation cancelled", true);
                                return;
                            }
                            compensate(player, compensation, compensatedPlayers, title);
                        });
                    } else {
                        compensate(player, compensation, compensatedPlayers, null);
                    }
                }
        );
        menu.openForPlayer(player);
    }

    public static void compensate(Player player, LinkedHashMap<Currencies, Long> compensation, List<DatabasePlayer> compensatedPlayers, String title) {
        System.out.println(compensation);
        System.out.println(compensatedPlayers);
        if (compensatedPlayers.size() == 1) {
            Warlords.newChain()
                    .sync(() -> {
                        DatabasePlayer databasePlayer = compensatedPlayers.get(0);
                        DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
                        if (title == null) {
                            compensation.forEach(pveStats::addCurrency);
                        } else {
                            pveStats.getCompensationRewards().add(new CompensationReward(compensation, title));
                        }
                        ChatChannels.playerSpigotSendMessage(player, ChatChannels.DEBUG,
                                new ComponentBuilder()
                                        .appendHoverText(
                                                ChatColor.GREEN + "Compensated " + ChatColor.AQUA + databasePlayer.getName() +
                                                        ChatColor.GREEN + (title == null ? " directly" : " through the Rewards Inventory"),
                                                compensation.entrySet()
                                                        .stream()
                                                        .map(currenciesValues -> ChatColor.GRAY + " - " + currenciesValues.getKey()
                                                                .getCostColoredName(currenciesValues.getValue()))
                                                        .collect(Collectors.joining("\n"))
                                        )
                        );
                        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                        player.closeInventory();
                    }).execute();

        } else {
            Warlords.newChain()
                    .sync(() -> {
                        for (DatabasePlayer databasePlayer : compensatedPlayers) {
                            DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
                            if (title == null) {
                                compensation.forEach(pveStats::addCurrency);
                            } else {
                                pveStats.getCompensationRewards().add(new CompensationReward(compensation, title));
                            }
                            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                        }
                        ChatChannels.sendDebugMessage(player,
                                ChatColor.GREEN + "All " + ChatColor.AQUA + compensatedPlayers.size() + " players " +
                                        ChatColor.GREEN + "were given compensation" + (title == null ? " directly" : " through the Rewards Inventory"),
                                true
                        );
                        player.closeInventory();
                    })
                    .execute();
        }
    }

    @Default
    public void openCompensateMenu(Player player) {
        if (LOADING.contains(player.getUniqueId())) {
            ChatChannels.sendDebugMessage(player, ChatColor.RED + "You are already loading a compensation menu!", true);
            return;
        }
        ChatChannels.sendDebugMessage(player, ChatColor.GREEN + "Loading compensation menu! Do not do anything", true);
        LOADING.add(player.getUniqueId());
        Warlords.newChain()
                .asyncFirst(() -> DatabaseManager.playerService.findAll(PlayersCollections.LIFETIME))
                .syncLast(unfilteredPlayers -> {
                    List<DatabasePlayer> databasePlayers = unfilteredPlayers.stream()
                            .filter(databasePlayer -> databasePlayer.getLastLogin() != null &&
                                    databasePlayer.getLastLogin().isAfter(Instant.now().minus(30, ChronoUnit.DAYS)))
                            .collect(Collectors.toList());
                    openCompensateMenu(player, new LinkedHashMap<>(), databasePlayers);
                    LOADING.remove(player.getUniqueId());
                }).execute();
    }

}
