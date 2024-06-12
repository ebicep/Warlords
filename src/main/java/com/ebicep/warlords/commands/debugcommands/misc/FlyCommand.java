package com.ebicep.warlords.commands.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.Comparator;

@CommandAlias("fly")
@CommandPermission("group.administrator")
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
                Component.text(otherPlayer.getName(), NamedTextColor.AQUA)
                         .append(Component.text(" Fly " + (allowFlight ? "Enabled" : "Disabled"), allowFlight ? NamedTextColor.GREEN : NamedTextColor.RED))
        );
    }

    @Subcommand("speed")
    @Description("Sets the fly speed")
    public void speed(Player player, @Optional @Flags("other") Player otherPlayer, @Conditions("limits:min=-1,max=1") float speed) {
        if (otherPlayer == null) {
            otherPlayer = player;
        }
        otherPlayer.setFlySpeed(speed);
        ChatChannels.sendDebugMessage(
                player,
                Component.text(otherPlayer.getName(), NamedTextColor.AQUA)
                         .append(Component.text(" Fly Speed set to " + speed, NamedTextColor.GREEN))
        );
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }

}