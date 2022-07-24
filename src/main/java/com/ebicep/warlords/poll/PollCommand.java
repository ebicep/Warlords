package com.ebicep.warlords.poll;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.BaseCommand;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class PollCommand implements CommandExecutor {

    public static void sendPollMessage(Player player, String message) {
        ChatUtils.sendMessageToPlayer(player, message, ChatColor.RED, true);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        //poll answer 3d8j2 2
        //poll end 3d8j2

        Player player = BaseCommand.requirePlayer(sender);

        if (player == null) {
            return true;
        }

        if (args.length <= 1) {
            sendPollMessage(player, ChatColor.RED + "Invalid Arguments!");
            return true;
        }

        String input = args[0];
        String pollID = args[1];
        Optional<AbstractPoll<?>> optionalPoll = AbstractPoll.getPoll(pollID);
        if (!optionalPoll.isPresent()) {
            sendPollMessage(player, ChatColor.RED + "Invalid Arguments!");
            return true;
        }

        AbstractPoll<?> poll = optionalPoll.get();

        switch (input) {
            case "answer": {
                if (args.length <= 2) {
                    sendPollMessage(player, ChatColor.RED + "Invalid Arguments!");
                    return true;
                }
                try {
                    int answer = Integer.parseInt(args[2]);
                    HashMap<UUID, Integer> playerAnsweredWithOption = poll.getPlayerAnsweredWithOption();
                    if (playerAnsweredWithOption.containsKey(player.getUniqueId())) {
                        if (playerAnsweredWithOption.get(player.getUniqueId()) == answer) {
                            sendPollMessage(player, ChatColor.RED + "You already voted for " + ChatColor.GOLD + poll.getOptions().get(answer - 1) + ChatColor.RED + "!");
                        } else {
                            playerAnsweredWithOption.put(player.getUniqueId(), answer);
                            sendPollMessage(player, ChatColor.GREEN + "You changed your vote to " + ChatColor.GOLD + poll.getOptions().get(answer - 1) + ChatColor.GREEN + "!");
                        }
                    } else if (answer > 0 && answer <= poll.getOptions().size()) {
                        playerAnsweredWithOption.put(player.getUniqueId(), answer);
                        sendPollMessage(player, ChatColor.GREEN + "You voted for " + ChatColor.GOLD + poll.getOptions().get(answer - 1) + ChatColor.GREEN + "!");
                    } else {
                        sendPollMessage(player, ChatColor.RED + "Invalid Arguments!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    sendPollMessage(player, ChatColor.RED + "Invalid Arguments!");
                }
                break;
            }
            case "end": {
                if (!player.hasPermission("warlords.poll.end")) {
                    sendPollMessage(player, ChatColor.RED + "Invalid permissions!");
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
