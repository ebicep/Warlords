package com.ebicep.warlords.commands.debugcommands.misc;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.BaseCommand;
import com.ebicep.warlords.party.PartyPlayer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

public class GetPlayersCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = BaseCommand.requirePlayer(commandSender);
        if (player != null) {
            Warlords.partyManager.getPartyFromAny(player.getUniqueId())
                    .ifPresent(party -> {
                        TextComponent message = new TextComponent(ChatColor.GREEN.toString() + ChatColor.UNDERLINE + ChatColor.BOLD + "CLICK ME FOR PLAYERS");
                        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Copy the URL without (https://)").create()));
                        message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://" + party.getPartyPlayers().stream()
                                .map(PartyPlayer::getUuid)
                                .map(Bukkit::getOfflinePlayer)
                                .map(OfflinePlayer::getName)
                                .collect(Collectors.joining(","))
                        ));
                        player.spigot().sendMessage(message);
                    });
        }
        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("getplayers").setExecutor(this);
    }
}
