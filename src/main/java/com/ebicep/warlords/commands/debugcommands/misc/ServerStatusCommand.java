package com.ebicep.warlords.commands.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

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
            ChatChannels.sendDebugMessage(issuer, Component.text("Server status is now enabled.", NamedTextColor.GREEN));
        } else {
            ChatChannels.sendDebugMessage(issuer, Component.text("Server status is now disabled.", NamedTextColor.RED));
        }
    }

}
