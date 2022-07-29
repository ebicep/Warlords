package com.ebicep.warlords.commands2.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.ebicep.warlords.commands2.miscellaneouscommands.ChatChannelCommand;
import org.bukkit.ChatColor;

@CommandAlias("recordgames")
@CommandPermission("warlords.game.recordgames")
public class RecordGamesCommand extends BaseCommand {

    public static boolean recordGames = true;

    @Default
    @Description("Toggles recording of games")
    public void recordGames(CommandIssuer issuer) {
        if (recordGames) {
            recordGames = false;
            ChatChannelCommand.sendDebugMessage(issuer, ChatColor.RED + "All games from now on will not be recorded!");
        } else {
            recordGames = true;
            ChatChannelCommand.sendDebugMessage(issuer, ChatColor.GREEN + "All games from now on will be recorded!");
        }
    }
}
