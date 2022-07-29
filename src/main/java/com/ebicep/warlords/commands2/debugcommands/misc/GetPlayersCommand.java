package com.ebicep.warlords.commands2.debugcommands.misc;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.party.PartyPlayer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

@CommandAlias("getplayers")
public class GetPlayersCommand extends co.aikar.commands.BaseCommand {

    @Default
    @Description("Get list of players in your party")
    public void getPlayers(@Conditions("requireParty") Player player) {
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

}
