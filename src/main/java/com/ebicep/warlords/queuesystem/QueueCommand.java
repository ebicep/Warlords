package com.ebicep.warlords.queuesystem;

import com.ebicep.warlords.Warlords;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class QueueCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (args.length == 0) {
            sender.sendMessage(QueueManager.getQueue());
            return true;
        }

        Player player = ((Player) sender);

        switch (args[0]) {
            case "join": {
                if (Warlords.partyManager.inAParty(player.getUniqueId())) {
                    sender.sendMessage(ChatColor.RED + "You cannot join the queue if you are in a party!");
                    return true;
                }
                if (QueueManager.queue.contains(player.getUniqueId())) {
                    sender.sendMessage(ChatColor.RED + "You are already in the queue!");
                } else {
                    QueueManager.addPlayerToQueue(sender.getName(), false);
                    QueueManager.removePlayerFromFutureQueue(sender.getName());
                    sender.sendMessage(ChatColor.GREEN + "You are now #" + QueueManager.queue.size() + " in queue!");
                    QueueManager.sendNewQueue();
                }
                return true;
            }
            case "leave": {
                QueueManager.removePlayerFromQueue(sender.getName());
                sender.sendMessage(ChatColor.RED + "You left the queue!");
                QueueManager.sendNewQueue();
                return true;
            }
            case "add": {
                if (sender.hasPermission("warlords.queue.clear")) {
                    if (args.length == 1) {
                        sender.sendMessage(ChatColor.RED + "Invalid player!");
                        return true;
                    }
                    String playerToAdd = args[1];
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerToAdd);
                    if (offlinePlayer == null) {
                        sender.sendMessage(ChatColor.RED + "Invalid player!");
                        return true;
                    }

                    QueueManager.addPlayerToQueue(playerToAdd, false);
                    sender.sendMessage(QueueManager.getQueue());
                    QueueManager.sendNewQueue();
                } else {
                    sender.sendMessage(ChatColor.RED + "Insufficient Permissions");
                }
            }
            case "remove": {
                if (sender.hasPermission("warlords.queue.clear")) {
                    if (args.length == 1) {
                        sender.sendMessage(ChatColor.RED + "Invalid queue number!");
                        return true;
                    }
                    if (!NumberUtils.isNumber(args[1])) {
                        sender.sendMessage(ChatColor.RED + "Invalid queue number!");
                        return true;
                    }

                    int queuePos = Integer.parseInt(args[1]);
                    if (queuePos > QueueManager.queue.size() || queuePos < 1) {
                        sender.sendMessage(ChatColor.RED + "Invalid queue number!");
                        return true;
                    }

                    QueueManager.queue.remove(queuePos - 1);
                    sender.sendMessage(QueueManager.getQueue());
                    QueueManager.sendNewQueue();
                } else {
                    sender.sendMessage(ChatColor.RED + "Insufficient Permissions");
                }
                return true;
            }
            case "clear": {
                if (sender.hasPermission("warlords.queue.clear")) {
                    QueueManager.queue.clear();
                    QueueManager.futureQueue.clear();
                    QueueManager.sendNewQueue();
                    sender.sendMessage(ChatColor.GREEN + "Queue cleared");
                } else {
                    sender.sendMessage(ChatColor.RED + "Insufficient Permissions");
                }
                return true;
            }
            default: {
                sender.sendMessage(ChatColor.RED + "Invalid Arguments (-queue join/leave)");
                return true;
            }
        }
    }

    public void register(Warlords instance) {
        instance.getCommand("queue").setExecutor(this);
    }
}
