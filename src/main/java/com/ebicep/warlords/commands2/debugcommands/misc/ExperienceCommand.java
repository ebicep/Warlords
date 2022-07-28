package com.ebicep.warlords.commands2.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Subcommand;
import com.ebicep.warlords.commands2.DatabasePlayerFuture;
import com.ebicep.warlords.commands2.miscellaneouscommands.ChatChannelCommand;
import com.ebicep.warlords.database.DatabaseManager;
import org.bukkit.ChatColor;

import java.util.concurrent.CompletionStage;

@CommandAlias("experience")
@CommandPermission("warlords.exp.give")
public class ExperienceCommand extends BaseCommand {

    @Subcommand("add")
    @Conditions("database:player")
    public CompletionStage<?> add(CommandIssuer issuer, DatabasePlayerFuture databasePlayerFuture, @Conditions("limits:min=0,max=10000") Integer amount) {
        return databasePlayerFuture.getFuture().thenAccept(databasePlayer -> {
            ChatChannelCommand.sendDebugMessage(issuer, ChatColor.GREEN + "Added " + amount + " universal experience to " + databasePlayer.getName());
            databasePlayer.setExperience(databasePlayer.getExperience() + amount);
            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
        });
    }

    @Subcommand("subtract")
    @Conditions("database:player")
    public CompletionStage<?> subtract(CommandIssuer issuer, DatabasePlayerFuture databasePlayerFuture, @Conditions("limits:min=0,max=10000") Integer amount) {
        return databasePlayerFuture.getFuture().thenAccept(databasePlayer -> {
            ChatChannelCommand.sendDebugMessage(issuer, ChatColor.GREEN + "Subtracted " + amount + " universal experience to " + databasePlayer.getName());
            databasePlayer.setExperience(databasePlayer.getExperience() - amount);
            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
        });
    }

}
