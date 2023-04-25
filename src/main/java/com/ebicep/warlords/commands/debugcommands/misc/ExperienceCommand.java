package com.ebicep.warlords.commands.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.commands.DatabasePlayerFuture;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Comparator;
import java.util.concurrent.CompletionStage;

@CommandAlias("experience|exp")
@CommandPermission("warlords.exp.give")
@Conditions("database:player")
public class ExperienceCommand extends BaseCommand {

    @Subcommand("add")
    public CompletionStage<?> add(CommandIssuer issuer, DatabasePlayerFuture databasePlayerFuture, @Conditions("limits:min=0,max=10000") Integer amount) {
        return databasePlayerFuture.future().thenAccept(databasePlayer -> {
            ChatChannels.sendDebugMessage(issuer, Component.text("Added " + amount + " universal experience to " + databasePlayer.getName(), NamedTextColor.GREEN));
            databasePlayer.setExperience(databasePlayer.getExperience() + amount);
            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
        });
    }

    @Subcommand("subtract")
    public CompletionStage<?> subtract(CommandIssuer issuer, DatabasePlayerFuture databasePlayerFuture, @Conditions("limits:min=0,max=10000") Integer amount) {
        return databasePlayerFuture.future().thenAccept(databasePlayer -> {
            ChatChannels.sendDebugMessage(issuer, Component.text("Subtracted " + amount + " universal experience to " + databasePlayer.getName(), NamedTextColor.GREEN));
            databasePlayer.setExperience(databasePlayer.getExperience() - amount);
            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
        });
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }
}
