package com.ebicep.warlords.party.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.ebicep.jda.BotManager;
import com.ebicep.warlords.party.Party;
import com.ebicep.warlords.party.PartyManager;
import com.ebicep.warlords.util.chat.ChatUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("stream")
@CommandPermission("warlords.party.stream")
public class StreamCommand extends BaseCommand {

    @Default
    @Description("Creates a new party where anyone can join")
    public void stream(@Conditions("party:false") Player player) {
        Party party = new Party(player.getUniqueId(), true);
        PartyManager.PARTIES.add(party);

        party.sendMessageToAllPartyPlayers(
                ChatColor.GREEN + "You created a public party! Players can join with\n" +
                        ChatColor.GOLD + ChatColor.BOLD + "/party join " + player.getName(),
                ChatColor.BLUE, true
        );

        Bukkit.getOnlinePlayers().stream()
              .filter(p -> p.getUniqueId() != player.getUniqueId())
              .forEach(onlinePlayer -> {
                  ChatUtils.sendCenteredMessage(onlinePlayer, ChatColor.BLUE.toString() + ChatColor.BOLD + "------------------------------------------");
                  ChatUtils.sendCenteredMessage(onlinePlayer, ChatColor.AQUA + player.getName() + ChatColor.YELLOW + " created a public party!");
                  ChatUtils.sendCenteredMessage(onlinePlayer, Component.text(ChatColor.GOLD.toString() + ChatColor.BOLD + "Click here to join!")
                                                                       .clickEvent(ClickEvent.runCommand("/party join " + player.getName()))
                  );
                  ChatUtils.sendCenteredMessage(onlinePlayer, ChatColor.BLUE.toString() + ChatColor.BOLD + "------------------------------------------");
              });

        BotManager.sendMessageToStatusChannel("[PARTY] **" + player.getName() + "** created a public party! /p join " + player.getName());
    }
}
