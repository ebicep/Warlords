package com.ebicep.warlords.commands.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import com.ebicep.warlords.commands.DatabasePlayerFuture;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.pve.rewards.types.PatreonReward;
import com.ebicep.warlords.util.chat.ChatChannels;
import org.bukkit.ChatColor;

import java.time.Month;
import java.time.Year;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.concurrent.CompletionStage;

@CommandAlias("patreon")
@CommandPermission("group.administrator")
public class PatreonCommand extends BaseCommand {

    @Subcommand("give")
    public CompletionStage<?> add(CommandIssuer issuer, DatabasePlayerFuture databasePlayerFuture, Month month, @Optional Year year) {
        if (year == null) {
            year = Year.from(ZonedDateTime.now());
        }
        Year finalYear = year;
        return databasePlayerFuture.getFuture().thenAccept(databasePlayer -> {
            boolean given = PatreonReward.giveMonthlyPatreonRewards(databasePlayer, month, finalYear);
            ChatChannels.sendDebugMessage(issuer,
                    given ? ChatColor.GREEN + "Gave " +
                            ChatColor.LIGHT_PURPLE + finalYear.getValue() + " " + month.getDisplayName(TextStyle.FULL, Locale.ENGLISH) +
                            ChatColor.GREEN + " Patreon reward to " + ChatColor.AQUA + databasePlayer.getName() :
                            ChatColor.AQUA + databasePlayer.getName() + ChatColor.RED + " has already received their monthly Patreon reward",
                    true
            );
            PatreonReward.givePatreonFutureMessage(databasePlayer, month, finalYear);
            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
        });
    }
}
