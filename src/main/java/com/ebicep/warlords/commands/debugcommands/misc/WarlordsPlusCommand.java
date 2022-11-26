package com.ebicep.warlords.commands.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.util.chat.ChatChannels;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

@CommandAlias("wps")
@CommandPermission("group.administrator")
public class WarlordsPlusCommand extends BaseCommand {

    public static final Set<UUID> UUIDS = new HashSet<>();
    public static boolean enabled = true;

    @Subcommand("add")
    public void add(CommandIssuer issuer, @Flags("@other") Player player) {
        if (UUIDS.contains(player.getUniqueId())) {
            ChatChannels.sendDebugMessage(issuer, ChatColor.RED + "Player already has access", true);
            return;
        }
        UUIDS.add(player.getUniqueId());
        ChatChannels.sendDebugMessage(issuer, ChatColor.GREEN + "Added player to access list", true);
    }

    @Subcommand("remove")
    public void remove(CommandIssuer issuer, @Flags("@other") Player player) {
        if (!UUIDS.contains(player.getUniqueId())) {
            ChatChannels.sendDebugMessage(issuer, ChatColor.RED + "Player does not have access", true);
            return;
        }
        UUIDS.remove(player.getUniqueId());
        ChatChannels.sendDebugMessage(issuer, ChatColor.GREEN + "Removed player from access list", true);
    }

    @Subcommand("toggle")
    public void toggle(CommandIssuer issuer) {
        enabled = !enabled;
        if (enabled) {
            ChatChannels.sendDebugMessage(issuer, ChatColor.GREEN + "WPS Enabled", true);
        } else {
            ChatChannels.sendDebugMessage(issuer, ChatColor.RED + "WPS Disabled", true);
        }
    }

    @Subcommand("list")
    public void list(CommandIssuer issuer) {
        ChatChannels.sendDebugMessage(issuer,
                ChatColor.GREEN + UUIDS.stream()
                        .map(Bukkit::getOfflinePlayer)
                        .filter(Objects::nonNull)
                        .map(OfflinePlayer::getName)
                        .collect(Collectors.joining(", ")),
                true
        );
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }

}
