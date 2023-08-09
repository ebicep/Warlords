package com.ebicep.warlords.commands.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
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
    public void add(CommandIssuer issuer, @Flags("other") Player player) {
        if (UUIDS.contains(player.getUniqueId())) {
            ChatChannels.sendDebugMessage(issuer, Component.text("Player already has access", NamedTextColor.RED));
            return;
        }
        UUIDS.add(player.getUniqueId());
        ChatChannels.sendDebugMessage(issuer, Component.text("Added player to access list", NamedTextColor.GREEN));
    }

    @Subcommand("remove")
    public void remove(CommandIssuer issuer, @Flags("other") Player player) {
        if (!UUIDS.contains(player.getUniqueId())) {
            ChatChannels.sendDebugMessage(issuer, Component.text("Player does not have access", NamedTextColor.RED));
            return;
        }
        UUIDS.remove(player.getUniqueId());
        ChatChannels.sendDebugMessage(issuer, Component.text("Removed player from access list", NamedTextColor.GREEN));
    }

    @Subcommand("toggle")
    public void toggle(CommandIssuer issuer) {
        enabled = !enabled;
        if (enabled) {
            ChatChannels.sendDebugMessage(issuer, Component.text("WPS Enabled", NamedTextColor.GREEN));
        } else {
            ChatChannels.sendDebugMessage(issuer, Component.text("WPS Disabled", NamedTextColor.RED));
        }
    }

    @Subcommand("list")
    public void list(CommandIssuer issuer) {
        ChatChannels.sendDebugMessage(issuer,
                Component.text(UUIDS.stream()
                                    .map(Bukkit::getOfflinePlayer)
                                    .filter(Objects::nonNull)
                                    .map(OfflinePlayer::getName)
                                    .collect(Collectors.joining(", ")), NamedTextColor.GREEN)
        );
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }

}
