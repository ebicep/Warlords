package com.ebicep.warlords.commands.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.util.chat.ChatChannels;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@CommandAlias("mute")
@CommandPermission("warlords.player.mute")
public class MuteCommand extends BaseCommand {

    public static final HashMap<UUID, Boolean> MUTED_PLAYERS = new HashMap<>();

    @Default
    @CommandCompletion("@players")
    @Description("Mutes a player")
    public void mute(CommandIssuer issuer, @Values("@players") @Flags("other") Player player) {
        UUID uuid = player.getUniqueId();
        String name = player.getName();
        if (MUTED_PLAYERS.getOrDefault(uuid, false)) {
            ChatChannels.sendDebugMessage(issuer, ChatColor.RED + name + " is already muted");
            return;
        }
        MUTED_PLAYERS.put(uuid, true);
        ChatChannels.sendDebugMessage(issuer, ChatColor.GREEN + "Muted " + name);
    }

    @CommandAlias("unmute")
    @CommandCompletion("@players")
    @Description("Unmutes a player")
    public void unmute(CommandIssuer issuer, @Values("@players") @Flags("other") Player player) {
        UUID uuid = player.getUniqueId();
        String name = player.getName();
        if (!MUTED_PLAYERS.getOrDefault(uuid, false)) {
            ChatChannels.sendDebugMessage(issuer, ChatColor.RED + name + " is not muted");
            return;
        }
        MUTED_PLAYERS.put(uuid, false);
        ChatChannels.sendDebugMessage(issuer, ChatColor.GREEN + "Unmuted " + name);
    }

    @CommandAlias("mutelist")
    @Description("Shows the list of muted players")
    public void muteList(CommandIssuer issuer) {
        String mutedList = MUTED_PLAYERS.entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(uuidBooleanEntry -> Bukkit.getOfflinePlayer(uuidBooleanEntry.getKey()).getName())
                .collect(Collectors.joining(","));
        if (mutedList.isEmpty()) {
            ChatChannels.sendDebugMessage(issuer, ChatColor.GREEN + "There are no muted players");
        } else {
            ChatChannels.sendDebugMessage(issuer, ChatColor.GREEN + "Muted Players: " + ChatColor.AQUA + mutedList);
        }
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }

}
