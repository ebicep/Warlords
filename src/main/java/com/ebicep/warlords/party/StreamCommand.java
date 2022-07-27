package com.ebicep.warlords.party;

import com.ebicep.jda.BotManager;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.BaseCommand;
import com.ebicep.warlords.util.chat.ChatUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class StreamCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!sender.hasPermission("warlords.party.stream")) {
            sender.sendMessage("§cYou do not have permission to do that.");
            return true;
        }

        Player player = BaseCommand.requirePlayer(sender);
        if (player == null) {
            return true;
        }

        if (Warlords.partyManager.inAParty(player.getUniqueId())) {
            Party.sendPartyMessage(player, ChatColor.RED + "You are already in a party");
            return true;
        }

        if (args.length == 0) {
            Party party = new Party(player.getUniqueId(), true);
            Warlords.partyManager.getParties().add(party);
            party.sendMessageToAllPartyPlayers(ChatColor.GREEN + "You created a public party! Players can join with\n" +
                            ChatColor.GOLD + ChatColor.BOLD + "/party join " + player.getName(),
                    ChatColor.BLUE, true);
            Bukkit.getOnlinePlayers().stream()
                    .filter(p -> p.getUniqueId() != player.getUniqueId())
                    .forEach(onlinePlayer -> {
                        ChatUtils.sendCenteredMessage(onlinePlayer, ChatColor.BLUE.toString() + ChatColor.BOLD + "------------------------------------------");
                        ChatUtils.sendCenteredMessage(onlinePlayer, ChatColor.AQUA + player.getName() + ChatColor.YELLOW + " created a public party!");
                        TextComponent message = new TextComponent(ChatColor.GOLD.toString() + ChatColor.BOLD + "Click here to join!");
                        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party join " + player.getName()));
                        ChatUtils.sendCenteredMessageWithEvents(onlinePlayer, Collections.singletonList(message));
                        ChatUtils.sendCenteredMessage(onlinePlayer, ChatColor.BLUE.toString() + ChatColor.BOLD + "------------------------------------------");
                    });
            BotManager.sendMessageToNotificationChannel("[PARTY] **" + player.getName() + "** created a public party! /p join " + player.getName(), true, false);
        }

        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("stream").setExecutor(this);
    }

}
