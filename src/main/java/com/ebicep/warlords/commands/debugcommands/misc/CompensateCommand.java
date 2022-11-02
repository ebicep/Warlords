package com.ebicep.warlords.commands.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.commands.DatabasePlayerFuture;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("compensate")
@CommandPermission("group.administrator")
public class CompensateCommand extends BaseCommand {

    @Default
    public void compensate(Player player, DatabasePlayerFuture databasePlayerFuture, Integer coins, Integer shards) {
        databasePlayerFuture.getFuture().thenAccept(databasePlayer -> {
            player.spigot().sendMessage(
                    new ComponentBuilder(ChatColor.GREEN + "Give ")
                            .appendHoverText(ChatColor.GOLD + "compensation",
                                    Currencies.COIN.getCostColoredName(coins) + "\n" +
                                            Currencies.SYNTHETIC_SHARD.getCostColoredName(shards)
                            )
                            .append(ChatColor.GREEN + " to " + ChatColor.AQUA + databasePlayer.getName())
                            .append(ChatColor.GREEN + " [CONFIRM]")
                            .appendClickEvent(ClickEvent.Action.RUN_COMMAND, "/compensate confirm " + databasePlayer.getName() + " " + coins + " " + shards)
                            .create()
            );
        });
    }

    @Private
    @Subcommand("confirm")
    public void compensateConfirm(Player player, DatabasePlayerFuture databasePlayerFuture, Integer coins, Integer shards) {
        databasePlayerFuture.getFuture().thenAccept(databasePlayer -> {
            DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
            pveStats.addCurrency(Currencies.COIN, coins);
            pveStats.addCurrency(Currencies.SYNTHETIC_SHARD, shards);
            player.spigot().sendMessage(
                    new ComponentBuilder(ChatColor.GREEN + "Gave ")
                            .appendHoverText(ChatColor.GOLD + "compensation", "Coins: " + coins + "\nShards: " + shards)
                            .append(ChatColor.GREEN + " to " + ChatColor.AQUA + databasePlayer.getName())
                            .create()
            );
        });
    }

}
