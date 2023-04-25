package com.ebicep.warlords.commands.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@CommandAlias("seeallchats")
@CommandPermission("group.administrator")
public class SeeAllChatsCommand extends BaseCommand {

    public static final Set<UUID> playerSeeAllChats = new HashSet<>();

    public static void addPlayerSeeAllChats(Set<Audience> players) {
        for (UUID playerSeeAllChat : playerSeeAllChats) {
            Player player = Bukkit.getPlayer(playerSeeAllChat);
            if (player != null) {
                players.add(player);
            }
        }
    }

    @Default
    @Description("Toggles seeing all chats")
    public void seeAllChats(Player player) {
        if (playerSeeAllChats.contains(player.getUniqueId())) {
            playerSeeAllChats.remove(player.getUniqueId());
            ChatChannels.sendDebugMessage(player, Component.text("You will no longer see all chats", NamedTextColor.GREEN));
        } else {
            playerSeeAllChats.add(player.getUniqueId());
            ChatChannels.sendDebugMessage(player, Component.text("You will now see all chats", NamedTextColor.GREEN));
        }
    }

    @Subcommand("clear")
    @Description("Clears all players that can see all chats")
    public void clear(CommandIssuer issuer) {
        playerSeeAllChats.clear();
        ChatChannels.sendDebugMessage(issuer, Component.text("All players that can see all chats have been cleared", NamedTextColor.GREEN));
    }

}
