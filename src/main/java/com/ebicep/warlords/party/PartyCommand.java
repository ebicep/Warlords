package com.ebicep.warlords.party;

import com.ebicep.warlords.Warlords;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class PartyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        switch(s) {
            case "party":
            case "p":
                if(args.length > 0) {
                    Player player = (Player) sender;
                    Optional<Party> currentParty = Warlords.partyManager.getPartyFromAny(player.getUniqueId());
                    String input = args[0];
                    switch(input) {
                        case "join":
                            if(args.length > 1) {
                                if(Warlords.partyManager.inAParty(player.getUniqueId())) {
                                    sender.sendMessage(ChatColor.RED + "You are already in a party");
                                    return true;
                                }
                                String playerWithParty = args[1];
                                if(Bukkit.getOnlinePlayers().stream().anyMatch(p -> p.getName().equalsIgnoreCase(playerWithParty))) {
                                    Player partyLeader = Bukkit.getOnlinePlayers().stream().filter(p -> p.getName().equalsIgnoreCase(playerWithParty)).findAny().get();
                                    Optional<Party> party = Warlords.partyManager.getPartyFromLeader(partyLeader.getUniqueId());
                                    if(party.isPresent()) {
                                        if(party.get().isOpen()) {
                                            party.get().join(player.getUniqueId());
                                        } else {
                                            sender.sendMessage(ChatColor.RED + "That party is not open!");
                                        }
                                    } else {
                                        sender.sendMessage(ChatColor.RED + "That player does not have a party!");
                                    }
                                } else {
                                    sender.sendMessage(ChatColor.RED + "Cannot find a player with that name!");
                                }
                            } else {
                                sender.sendMessage(ChatColor.RED + "Invalid Arguments!");
                            }
                            break;
                        case "leave":
                            if(currentParty.isPresent()) {
                                currentParty.get().leave(player.getUniqueId());
                                sender.sendMessage(ChatColor.GREEN + "You left the party");
                            } else{
                                sender.sendMessage(ChatColor.RED + "You are not in a party");
                            }
                            break;
                        case "disband":
                            if(currentParty.isPresent()) {
                                if(currentParty.get().getLeader().equals(player.getUniqueId())) {
                                    currentParty.get().disband();
                                } else {
                                    sender.sendMessage(ChatColor.RED + "You are not the party leader");
                                }
                            } else{
                                sender.sendMessage(ChatColor.RED + "You are not in a party");
                            }
                            break;
                        case "list":
                            if(currentParty.isPresent()) {
                                sender.sendMessage(ChatColor.GREEN + currentParty.get().getList());
                            } else {
                                sender.sendMessage(ChatColor.RED + "You are not in a party");
                            }
                            break;
                    }
                }
                break;
            case "pl":
                Bukkit.getServer().dispatchCommand(sender, "party list");
                break;
        }

        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("party").setExecutor(this);
    }

}