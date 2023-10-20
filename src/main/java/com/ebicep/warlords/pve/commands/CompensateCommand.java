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
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.rewards.types.CompensationReward;
import com.ebicep.warlords.util.bukkit.Colors;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.chat.ChatChannels;
import com.ebicep.warlords.util.chat.ChatUtils;
import io.github.rapha149.signgui.SignGUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static com.ebicep.warlords.menu.Menu.ACTION_CLOSE_MENU;
import static com.ebicep.warlords.menu.Menu.MENU_CLOSE;

@CommandAlias("compensate")
@CommandPermission("group.administrator")
public class CompensateCommand extends BaseCommand {

    public static void openCompensateMenu(Player player, LinkedHashMap<Spendable, Long> compensation, List<DatabasePlayer> compensatedPlayers) {
        if (compensatedPlayers == null || compensatedPlayers.isEmpty()) {
            ChatChannels.sendDebugMessage(player, Component.text("No players to compensate!", NamedTextColor.RED));
            return;
        }
        Menu menu = new Menu("Compensate", 9 * 6);

        int row = 1;
        int col = 1;
        for (Currencies currency : Currencies.VALUES) {
            menu.setItem(col, row,
                    new ItemBuilder(currency.item)
                            .name(currency.getColoredName())
                            .lore(Component.text(compensation.getOrDefault(currency, 0L), NamedTextColor.GREEN))
                            .get(),
                    (m, e) -> {
                        String[] text = new String[]{"", "", "", ""};
                        String[] currencyNameSplit = currency.name.split(" ");
                        System.arraycopy(currencyNameSplit, 0, text, 1, currencyNameSplit.length);
                        SignGUI.builder()
                               .setLines(text)
                               .setHandler((p, lines) -> {
                                   String amount = lines.getLine(0);
                                   try {
                                       int amountInt = Integer.parseInt(amount);
                                       compensation.put(currency, (long) amountInt);
                                   } catch (Exception exception) {
                                       p.sendMessage(Component.text("Invalid Amount", NamedTextColor.RED));
                                   }
                                   new BukkitRunnable() {
                                       @Override
                                       public void run() {
                                           openCompensateMenu(p, compensation, compensatedPlayers);
                                       }
                                   }.runTaskLater(Warlords.getInstance(), 1);
                                   return null;
                               }).build().open(player);
                    }
            );

            if (++col >= 8) {
                col = 1;
                row++;
            }
        }

        menu.setItem(3, 5,
                new ItemBuilder(Material.PLAYER_HEAD)
                        .name(Component.text("Player", NamedTextColor.GREEN))
                        .lore(Component.text(compensatedPlayers.size() == 1 ?
                                             compensatedPlayers.get(0).getName() :
                                             "All " + compensatedPlayers.size() + " Players", NamedTextColor.AQUA))
                        .get(),
                (m, e) -> {
                    SignGUI.builder()
                           .setLines("", "Enter", "Player", "Name")
                           .setHandler((p, lines) -> {
                               String playerName = lines.getLine(0);
                               for (DatabasePlayer databasePlayer : compensatedPlayers) {
                                   if (databasePlayer.getName().equalsIgnoreCase(playerName)) {
                                       openCompensateMenu(player, compensation, List.of(databasePlayer));
                                       return null;
                                   }
                               }
                               ChatChannels.sendDebugMessage(player,
                                       Component.text(playerName, NamedTextColor.AQUA).append(Component.text(" was not found for compensation", NamedTextColor.RED))
                               );
                               return null;
                           }).build().open(player);
                }
        );
        menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.setItem(5, 5,
                new ItemBuilder(Material.CHEST)
                        .name(Component.text("All Players", NamedTextColor.GREEN))
                        .lore(compensatedPlayers.stream()
                                                .map(databasePlayer -> Component.text(" - ", NamedTextColor.GRAY)
                                                                                .append(Component.text(databasePlayer.getName(), NamedTextColor.AQUA)))
                                                .collect(Component.toComponent(Component.empty())))
                        .get(),
                (m, e) -> {
                    openCompensateMenu(player, compensation, new ArrayList<>(compensatedPlayers));
                }
        );
        menu.setItem(8, 5,
                new ItemBuilder(Colors.GREEN.wool)
                        .name(Component.text("Confirm Compensate", NamedTextColor.GREEN))
                        .lore(compensation.entrySet()
                                          .stream()
                                          .map(currenciesValues -> Component.text(" - ", NamedTextColor.GRAY)
                                                                            .append(currenciesValues.getKey().getCostColoredName(currenciesValues.getValue())))
                                          .toArray(net.kyori.adventure.text.TextComponent[]::new))
                        .addLore(
                                Component.empty(),
                                Component.textOfChildren(
                                        Component.text("CLICK ", NamedTextColor.YELLOW, TextDecoration.BOLD),
                                        Component.text("to directly give through the Rewards Inventory", NamedTextColor.GREEN)
                                ),
                                Component.textOfChildren(
                                        Component.text("SHIFT-CLICK ", NamedTextColor.YELLOW, TextDecoration.BOLD),
                                        Component.text("to directly give rewards", NamedTextColor.GREEN)
                                )
                        )
                        .get(),
                (m, e) -> {
                    if (!e.isShiftClick()) {
                        SignGUI.builder()
                               .setLines("", "Enter Reward", "Title", "Blank to Cancel")
                               .setHandler((p, lines) -> {
                                   String title = lines.getLine(0);
                                   if (title.isEmpty()) {
                                       ChatChannels.sendDebugMessage(player, Component.text("Blank title, compensation cancelled", NamedTextColor.RED));
                                       return null;
                                   }
                                   new BukkitRunnable() {
                                       @Override
                                       public void run() {
                                           compensate(player, compensation, compensatedPlayers, title);
                                       }
                                   }.runTaskLater(Warlords.getInstance(), 1);
                                   return null;
                               }).build().open(player);
                    } else {
                        compensate(player, compensation, compensatedPlayers, null);
                    }
                }
        );
        menu.openForPlayer(player);
    }

    public static void compensate(Player player, LinkedHashMap<Spendable, Long> compensation, List<DatabasePlayer> compensatedPlayers, String title) {
        ChatUtils.MessageType.WARLORDS.sendMessage(compensation.toString());
        ChatUtils.MessageType.WARLORDS.sendMessage(compensatedPlayers.toString());
        if (compensatedPlayers.size() == 1) {
            Warlords.newChain()
                    .sync(() -> {
                        DatabasePlayer databasePlayer = compensatedPlayers.get(0);
                        DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
                        if (title == null) {
                            compensation.forEach((spendable, amount) -> spendable.addToPlayer(databasePlayer, amount));
                        } else {
                            pveStats.getCompensationRewards().add(new CompensationReward(compensation, title));
                        }
                        ChatChannels.playerSendMessage(player,
                                ChatChannels.DEBUG,
                                Component.text("Compensated ", NamedTextColor.GREEN)
                                         .append(Component.text(databasePlayer.getName(), NamedTextColor.AQUA))
                                         .append(Component.text(title == null ? " directly" : " through the Rewards Inventory"))
                                         .hoverEvent(HoverEvent.showText(
                                                 compensation.entrySet()
                                                             .stream()
                                                             .map(currenciesValues ->
                                                                     Component.text(" - ", NamedTextColor.GRAY)
                                                                              .append(currenciesValues.getKey().getCostColoredName(currenciesValues.getValue())))
                                                             .collect(Component.toComponent(Component.newline()))))
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
                                compensation.forEach((spendable, amount) -> spendable.addToPlayer(databasePlayer, amount));
                            } else {
                                pveStats.getCompensationRewards().add(new CompensationReward(compensation, title));
                            }
                            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                        }
                        ChatChannels.sendDebugMessage(player,
                                Component.text("All ", NamedTextColor.GREEN)
                                         .append(Component.text(compensatedPlayers.size() + " players ", NamedTextColor.AQUA))
                                         .append(Component.text("were given compensation" + (title == null ? " directly" : " through the Rewards Inventory")))
                        );
                        player.closeInventory();
                    })
                    .execute();
        }
    }

    @Default
    public void openCompensateMenu(Player player) {
        List<DatabasePlayer> databasePlayers = DatabaseManager.CACHED_PLAYERS
                .get(PlayersCollections.LIFETIME)
                .values()
                .stream()
                .filter(databasePlayer -> databasePlayer.getLastLogin() != null &&
                        databasePlayer.getLastLogin().isAfter(Instant.now().minus(30, ChronoUnit.DAYS)))
                .toList();
        openCompensateMenu(player, new LinkedHashMap<>(), databasePlayers);
    }

}
