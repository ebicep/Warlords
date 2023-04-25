package com.ebicep.warlords.commands.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
            ChatChannels.sendDebugMessage(issuer, Component.text(name + " is already muted", NamedTextColor.RED));
            return;
        }
        MUTED_PLAYERS.put(uuid, true);
        ChatChannels.sendDebugMessage(issuer, Component.text("Muted " + name, NamedTextColor.GREEN));
    }

    @CommandAlias("unmute")
    @CommandCompletion("@players")
    @Description("Unmutes a player")
    public void unmute(CommandIssuer issuer, @Values("@players") @Flags("other") Player player) {
        UUID uuid = player.getUniqueId();
        String name = player.getName();
        if (!MUTED_PLAYERS.getOrDefault(uuid, false)) {
            ChatChannels.sendDebugMessage(issuer, Component.text(name + " is not muted", NamedTextColor.RED));
            return;
        }
        MUTED_PLAYERS.put(uuid, false);
        ChatChannels.sendDebugMessage(issuer, Component.text("Unmuted " + name, NamedTextColor.GREEN));
    }

    @CommandAlias("mutelist")
    @Description("Shows the list of muted players")
    public void muteList(CommandIssuer issuer) {
        TextComponent.Builder mutedList = Component.text().color(NamedTextColor.AQUA);
        MUTED_PLAYERS.entrySet().stream()
                     .filter(Map.Entry::getValue)
                     .forEachOrdered(uuidBooleanEntry -> mutedList.append(Component.text(Bukkit.getOfflinePlayer(uuidBooleanEntry.getKey()).getName())));
        if (mutedList.children().size() == 0) {
            ChatChannels.sendDebugMessage(issuer, Component.text("There are no muted players", NamedTextColor.GREEN));
        } else {
            ChatChannels.sendDebugMessage(issuer, Component.text("Muted Players: ", NamedTextColor.GREEN)
                                                           .append(mutedList));
        }
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }

}
