package com.ebicep.warlords.commands.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.util.chat.ChatChannels;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Comparator;

@CommandAlias("fly")
@CommandPermission("minecraft.command.op|group.administrator")
public class FlyCommand extends BaseCommand {

    @Default
    @Description("Toggles fly")
    public void fly(Player player, @Optional @Flags("other") Player otherPlayer) {
        if (otherPlayer == null) {
            otherPlayer = player;
        }
        otherPlayer.setAllowFlight(!otherPlayer.getAllowFlight());
        boolean allowFlight = otherPlayer.getAllowFlight();
        ChatChannels.sendDebugMessage(
                player,
                ChatColor.AQUA + otherPlayer.getName() + (allowFlight ? ChatColor.GREEN : ChatColor.RED) +
                        " Fly " + (allowFlight ? "Enabled" : "Disabled"),
                true
        );
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }

}