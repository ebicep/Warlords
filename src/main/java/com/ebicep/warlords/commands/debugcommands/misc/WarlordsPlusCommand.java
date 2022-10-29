package com.ebicep.warlords.commands.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import com.ebicep.warlords.util.chat.ChatChannels;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@CommandAlias("wps")
@CommandPermission("group.administrator")
public class WarlordsPlusCommand extends BaseCommand {

    public static final Set<UUID> UUIDS = new HashSet<>();
    public static boolean enabled = true;

    @Subcommand("add")
    public void add(CommandIssuer issuer, @Flags("@other") Player player) {
        if (UUIDS.contains(player.getUniqueId())) {
            ChatChannels.sendDebugMessage(issuer, "Player already has access", true);
            return;
        }
        UUIDS.add(player.getUniqueId());
        ChatChannels.sendDebugMessage(issuer, "Added player to access list", true);
    }

    @Subcommand("remove")
    public void remove(CommandIssuer issuer, @Flags("@other") Player player) {
        if (!UUIDS.contains(player.getUniqueId())) {
            ChatChannels.sendDebugMessage(issuer, "Player does not have access", true);
            return;
        }
        UUIDS.remove(player.getUniqueId());
        ChatChannels.sendDebugMessage(issuer, "Removed player from access list", true);
    }

    @Subcommand("toggle")
    public void toggle(CommandIssuer issuer) {
        enabled = !enabled;
        if (enabled) {
            ChatChannels.sendDebugMessage(issuer, "WPS Enabled", true);
        } else {
            ChatChannels.sendDebugMessage(issuer, "WPS Disabled", true);
        }
    }

}
