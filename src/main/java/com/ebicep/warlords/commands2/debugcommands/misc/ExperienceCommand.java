package com.ebicep.warlords.commands2.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.commands2.DatabasePlayerFuture;
import com.ebicep.warlords.commands2.miscellaneouscommands.ChatCommand;
import com.ebicep.warlords.database.DatabaseManager;
import org.bukkit.ChatColor;

import java.util.concurrent.CompletionStage;

@CommandAlias("experience")
@CommandPermission("warlords.exp.give")
@Conditions("database:player")
public class ExperienceCommand extends BaseCommand {

    @Subcommand("add")
    public CompletionStage<?> add(CommandIssuer issuer, DatabasePlayerFuture databasePlayerFuture, @Conditions("limits:min=0,max=10000") Integer amount) {
        return databasePlayerFuture.getFuture().thenAccept(databasePlayer -> {
            ChatCommand.sendDebugMessage(issuer, ChatColor.GREEN + "Added " + amount + " universal experience to " + databasePlayer.getName());
            databasePlayer.setExperience(databasePlayer.getExperience() + amount);
            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
        });
    }

    @Subcommand("subtract")
    public CompletionStage<?> subtract(CommandIssuer issuer, DatabasePlayerFuture databasePlayerFuture, @Conditions("limits:min=0,max=10000") Integer amount) {
        return databasePlayerFuture.getFuture().thenAccept(databasePlayer -> {
            ChatCommand.sendDebugMessage(issuer, ChatColor.GREEN + "Subtracted " + amount + " universal experience to " + databasePlayer.getName());
            databasePlayer.setExperience(databasePlayer.getExperience() - amount);
            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
        });
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.showHelp();
    }
}
