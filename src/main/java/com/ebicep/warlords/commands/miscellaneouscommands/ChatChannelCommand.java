package com.ebicep.warlords.commands.miscellaneouscommands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.util.chat.ChatChannels;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashSet;
import java.util.UUID;

public class ChatChannelCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        ChatChannels chatChannel = Warlords.playerChatChannels.get(uuid);
        switch(s.toLowerCase()){
            case "chat":
                if(args.length > 0) {
                    switch (args[0].toLowerCase()) {
                        case "a":
                        case "all":
                            if(chatChannel == ChatChannels.ALL) {
                                player.sendMessage(ChatColor.RED + "You are already in this channel");
                            } else {
                                Warlords.playerChatChannels.put(uuid, ChatChannels.ALL);
                                player.sendMessage(ChatColor.GREEN + "You are now in the" + ChatColor.GOLD + " ALL " + ChatColor.GREEN + "channel");
                            }
                            return true;
                        case "p":
                        case "party":
                            if(Warlords.partyManager.inAParty(uuid)) {
                                if(chatChannel == ChatChannels.PARTY) {
                                    player.sendMessage(ChatColor.RED + "You are already in this channel");
                                } else {
                                    Warlords.playerChatChannels.put(uuid, ChatChannels.PARTY);
                                    player.sendMessage(ChatColor.GREEN + "You are now in the" + ChatColor.GOLD + " PARTY " + ChatColor.GREEN + "channel");
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "You must be in a party to join the party channel");
                            }
                            return true;
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Invalid Option! /chat (all/party)");
                }
                break;
            case "achat":
            case "ac":
            case "pchat":
            case "pc": {
                String input = "";
                for (int i = 0; i < args.length; i++) {
                    input += args[i] + " ";
                }
                if(!input.isEmpty()) {
                    AsyncPlayerChatEvent event = new AsyncPlayerChatEvent(true, player, input, new HashSet<>(Bukkit.getOnlinePlayers()));
                    if(s.equalsIgnoreCase("achat") || s.equalsIgnoreCase("ac")) {
                        Warlords.playerChatChannels.put(uuid, ChatChannels.ALL);
                    } else {
                        if(!Warlords.partyManager.inAParty(uuid)) {
                            player.sendMessage(ChatColor.RED + "You must be in a party to type in the party channel");
                            return true;
                        }
                        Warlords.playerChatChannels.put(uuid, ChatChannels.PARTY);
                    }
                    Bukkit.getServer().getScheduler().runTaskAsynchronously(Warlords.getInstance(), () -> {
                        Bukkit.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            Bukkit.getServer().getScheduler().callSyncMethod(Warlords.getInstance(), () -> {
                                String message = String.format(event.getFormat(), event.getPlayer().getName(), event.getMessage());
                                Bukkit.getServer().getConsoleSender().sendMessage(message);
                                for (Player p : event.getRecipients()) {
                                    p.sendMessage(message);
                                }
                                Warlords.playerChatChannels.put(uuid, chatChannel == null ? ChatChannels.ALL : chatChannel);
                                return null;
                            });
                        }
                    });
                } else {
                    sender.sendMessage(ChatColor.RED + "Invalid Option! /" + s.toLowerCase() + " message");
                }
                return true;
            }
        }
        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("chatchannelcommand").setExecutor(this);
    }

}
