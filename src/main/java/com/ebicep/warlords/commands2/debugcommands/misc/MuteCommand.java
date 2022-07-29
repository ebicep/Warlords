package com.ebicep.warlords.commands2.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.commands2.miscellaneouscommands.ChatChannelCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@CommandAlias("mute")
@CommandPermission("warlords.player.mute")
public class MuteCommand extends BaseCommand {

    public static HashMap<UUID, Boolean> mutedPlayers = new HashMap<>();

    @Default
    @CommandCompletion("@players")
    @Description("Mutes a player")
    public void mute(CommandIssuer issuer, @Values("@players") Player player) {
        UUID uuid = player.getUniqueId();
        String name = player.getName();
        if (mutedPlayers.getOrDefault(uuid, false)) {
            ChatChannelCommand.sendDebugMessage(issuer, ChatColor.RED + name + " is already muted");
            return;
        }
        mutedPlayers.put(uuid, true);
        ChatChannelCommand.sendDebugMessage(issuer, ChatColor.GREEN + "Muted " + name);
    }

    @CommandAlias("unmute")
    @CommandCompletion("@players")
    @Description("Unmutes a player")
    public void unmute(CommandIssuer issuer, @Values("@players") Player player) {
        UUID uuid = player.getUniqueId();
        String name = player.getName();
        if (!mutedPlayers.getOrDefault(uuid, false)) {
            ChatChannelCommand.sendDebugMessage(issuer, ChatColor.RED + name + " is not muted");
            return;
        }
        mutedPlayers.put(uuid, false);
        ChatChannelCommand.sendDebugMessage(issuer, ChatColor.GREEN + "Unmuted " + name);
    }

    @CommandAlias("mutelist")
    @Description("Shows the list of muted players")
    public void muteList(CommandIssuer issuer) {
        String mutedList = mutedPlayers.entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(uuidBooleanEntry -> Bukkit.getOfflinePlayer(uuidBooleanEntry.getKey()).getName())
                .collect(Collectors.joining(","));
        if (mutedList.isEmpty()) {
            ChatChannelCommand.sendDebugMessage(issuer, ChatColor.GREEN + "There are no muted players");
        } else {
            ChatChannelCommand.sendDebugMessage(issuer, ChatColor.GREEN + "Muted Players: " + ChatColor.AQUA + mutedList);
        }
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.showHelp();
    }

}
