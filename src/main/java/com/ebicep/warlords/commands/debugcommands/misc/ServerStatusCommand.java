package com.ebicep.warlords.commands.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.util.chat.ChatChannels;
import org.bukkit.ChatColor;

@CommandAlias("serverstatus")
@CommandPermission("warlords.game.serverstatus")
public class ServerStatusCommand extends BaseCommand {

    public static boolean enabled = true;

    @Default
    @CommandCompletion("@enabledisable")
    @Description("Enables/Disables server status")
    public void serverStatus(CommandIssuer issuer, @Values("@enabledisable") String option) {
        ServerStatusCommand.enabled = option.equals("enable");
        if (ServerStatusCommand.enabled) {
            ChatChannels.sendDebugMessage(issuer, ChatColor.GREEN + "Server status is now enabled.", true);
        } else {
            ChatChannels.sendDebugMessage(issuer, ChatColor.RED + "Server status is now disabled.", true);
        }
    }

}
