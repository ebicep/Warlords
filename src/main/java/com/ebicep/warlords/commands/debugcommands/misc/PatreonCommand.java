package com.ebicep.warlords.commands.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.commands.DatabasePlayerFuture;
import com.ebicep.warlords.database.repositories.timings.pojos.DatabaseTiming;
import com.ebicep.warlords.pve.rewards.types.PatreonReward;
import com.ebicep.warlords.util.chat.ChatChannels;
import org.bukkit.ChatColor;

import java.time.Instant;
import java.time.Month;
import java.time.Year;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.concurrent.CompletionStage;

@CommandAlias("patreon")
@CommandPermission("group.administrator")
public class PatreonCommand extends BaseCommand {

    @Subcommand("give")
    public CompletionStage<?> add(CommandIssuer issuer, DatabasePlayerFuture databasePlayerFuture, Month month, @Optional Year year) {
        if (year == null) {
            year = Year.from(Instant.now());
        }
        Year finalYear = year;
        return databasePlayerFuture.getFuture().thenAccept(databasePlayer -> {
            boolean given = PatreonReward.giveMonthlyPatreonRewards(databasePlayer, month, finalYear);
            ChatChannels.sendDebugMessage(issuer,
                    given ? ChatColor.GREEN + "Gave " +
                            ChatColor.LIGHT_PURPLE + month.getDisplayName(TextStyle.FULL, Locale.ENGLISH) + finalYear.getValue() +
                            ChatColor.GREEN + " Patreon reward to " + ChatColor.AQUA + databasePlayer.getName() :
                            ChatColor.AQUA + databasePlayer.getName() + ChatColor.RED + " has already received their monthly Patreon reward",
                    true
            );
        });
    }
}
