package com.ebicep.warlords.commands.miscellaneouscommands;

import com.ebicep.warlords.ChatChannels;
import com.ebicep.warlords.Warlords;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class ChatChannelCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        switch(s.toLowerCase()){
            case "chat":
                if(args.length > 0) {
                    switch (args[0].toLowerCase()) {
                        case "a":
                        case "all":
                            Warlords.playerChatChannels.put(uuid, ChatChannels.ALL);
                            player.sendMessage(ChatColor.GREEN + "You are now in the" + ChatColor.GOLD + " ALL " + ChatColor.GREEN + "channel");
                            return true;
                        case "p":
                        case "party":
                            if(Warlords.partyManager.inAParty(uuid)) {
                                Warlords.playerChatChannels.put(uuid, ChatChannels.PARTY);
                                player.sendMessage(ChatColor.GREEN + "You are now in the" + ChatColor.GOLD + " PARTY " + ChatColor.GREEN + "channel");
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
            case "ac": {
//                ChatChannels previousChannel = Warlords.playerChatChannels.get(uuid);
//                Warlords.playerChatChannels.put(uuid, ChatChannels.ALL);
//                Warlords.playerChatChannels.put(uuid, previousChannel);
                return true;
            }
            case "pchat":
            case "pc": {
//                ChatChannels previousChannel = Warlords.playerChatChannels.get(uuid);
//                Warlords.playerChatChannels.put(uuid, ChatChannels.PARTY);
//                player.chat("hehehehehe");
//                Warlords.playerChatChannels.put(uuid, previousChannel);
                return true;
            }
        }
        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("chatchannelcommand").setExecutor(this);
    }

}
