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

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        //poll answer 3d8j2 2
        //poll end 3d8j2

        Player player = BaseCommand.requirePlayer(sender);

        if (player == null) {
            return true;
        }

        if (args.length <= 1) {
            ChatUtils.sendMessageToPlayer(player, ChatColor.RED + "Invalid Arguments!", ChatColor.BLUE, true);
            return true;
        }

        String input = args[0];
        String pollID = args[1];
        Optional<AbstractPoll<?>> optionalPoll = AbstractPoll.getPoll(pollID);
        if (!optionalPoll.isPresent()) {
            ChatUtils.sendMessageToPlayer(player, ChatColor.RED + "Invalid Arguments!", ChatColor.BLUE, true);
            return true;
        }

        AbstractPoll<?> poll = optionalPoll.get();

        switch (input) {
            case "answer": {
                if (args.length <= 2) {
                    ChatUtils.sendMessageToPlayer(player, ChatColor.RED + "Invalid Arguments!", ChatColor.BLUE, true);
                    return true;
                }
                try {
                    int answer = Integer.parseInt(args[2]);
                    HashMap<UUID, Integer> playerAnsweredWithOption = poll.getPlayerAnsweredWithOption();
                    if (playerAnsweredWithOption.containsKey(player.getUniqueId())) {
                        if (playerAnsweredWithOption.get(player.getUniqueId()) == answer) {
                            ChatUtils.sendMessageToPlayer(player, ChatColor.RED + "You already voted for " + ChatColor.GOLD + poll.getOptions().get(answer - 1) + ChatColor.RED + "!", ChatColor.BLUE, true);
                        } else {
                            playerAnsweredWithOption.put(player.getUniqueId(), answer);
                            ChatUtils.sendMessageToPlayer(player, ChatColor.GREEN + "You changed your vote to " + ChatColor.GOLD + poll.getOptions().get(answer - 1) + ChatColor.GREEN + "!", ChatColor.BLUE, true);
                        }
                    } else if (answer > 0 && answer <= poll.getOptions().size()) {
                        playerAnsweredWithOption.put(player.getUniqueId(), answer);
                        ChatUtils.sendMessageToPlayer(player, ChatColor.GREEN + "You voted for " + ChatColor.GOLD + poll.getOptions().get(answer - 1) + ChatColor.GREEN + "!", ChatColor.BLUE, true);
                    } else {
                        ChatUtils.sendMessageToPlayer(player, ChatColor.RED + "Invalid Arguments!", ChatColor.BLUE, true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ChatUtils.sendMessageToPlayer(player, ChatColor.RED + "Invalid Arguments!", ChatColor.BLUE, true);
                }
                break;
            }
            case "end": {
                if (!player.hasPermission("warlords.poll.end")) {
                    ChatUtils.sendMessageToPlayer(player, ChatColor.RED + "Invalid permissions!", ChatColor.BLUE, true);
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
