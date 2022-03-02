package com.ebicep.warlords.poll;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.BaseCommand;
import com.ebicep.warlords.party.Party;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class PollCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        //poll answer 3d8j2 2
        //poll end 3d8j2

        Player player = BaseCommand.requirePlayer(sender);

        if (player == null) {
            return true;
        }

        if (args.length <= 1) {
            Party.sendMessageToPlayer(player, ChatColor.RED + "Invalid Arguments!", true, true);
            return true;
        }

        String input = args[0];
        String pollID = args[1];
        Optional<AbstractPoll<?>> optionalPoll = AbstractPoll.getPoll(pollID);
        if (!optionalPoll.isPresent()) {
            Party.sendMessageToPlayer(player, ChatColor.RED + "Invalid Arguments!", true, true);
            return true;
        }

        AbstractPoll<?> poll = optionalPoll.get();

        switch (input) {
            case "answer": {
                if (args.length <= 2) {
                    Party.sendMessageToPlayer(player, ChatColor.RED + "Invalid Arguments!", true, true);
                    return true;
                }
                try {
                    int answer = Integer.parseInt(args[2]);
                    HashMap<UUID, Integer> playerAnsweredWithOption = poll.getPlayerAnsweredWithOption();
                    if (playerAnsweredWithOption.containsKey(player.getUniqueId())) {
                        if (playerAnsweredWithOption.get(player.getUniqueId()) == answer) {
                            Party.sendMessageToPlayer(player, ChatColor.RED + "You already voted for " + ChatColor.GOLD + poll.getOptions().get(answer - 1) + ChatColor.RED + "!", true, true);
                        } else {
                            playerAnsweredWithOption.put(player.getUniqueId(), answer);
                            Party.sendMessageToPlayer(player, ChatColor.GREEN + "You changed your vote to " + ChatColor.GOLD + poll.getOptions().get(answer - 1) + ChatColor.GREEN + "!", true, true);
                        }
                    } else if (answer > 0 && answer <= poll.getOptions().size()) {
                        playerAnsweredWithOption.put(player.getUniqueId(), answer);
                        Party.sendMessageToPlayer(player, ChatColor.GREEN + "You voted for " + ChatColor.GOLD + poll.getOptions().get(answer - 1) + ChatColor.GREEN + "!", true, true);
                    } else {
                        Party.sendMessageToPlayer(player, ChatColor.RED + "Invalid Arguments!", true, true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Party.sendMessageToPlayer(player, ChatColor.RED + "Invalid Arguments!", true, true);
                }
                break;
            }
            case "end": {
                if (!player.hasPermission("warlords.poll.end")) {
                    Party.sendMessageToPlayer(player, ChatColor.RED + "Invalid permissions!", true, true);
                    return true;
                }
                poll.setTimeLeft(0);
                break;
            }
        }

        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("poll").setExecutor(this);
    }

}
