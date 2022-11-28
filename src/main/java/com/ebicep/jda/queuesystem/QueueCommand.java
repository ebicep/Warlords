package com.ebicep.jda.queuesystem;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Comparator;

@CommandAlias("queue")
public class QueueCommand extends BaseCommand {

    @Default
    @Description("Shows the queue")
    public void queue(Player player) {
        player.sendMessage(QueueManager.getQueue());
    }

    @Subcommand("join")
    @Description("Joins the queue")
    public void join(@Conditions("party:false") Player player) {//, @Optional String time) {
        if (QueueManager.queue.contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You are already in the queue!");
        } else {
            QueueManager.addPlayerToQueue(player.getName(), false);
            QueueManager.removePlayerFromFutureQueue(player.getName());
            player.sendMessage(ChatColor.GREEN + "You are now #" + QueueManager.queue.size() + " in queue!");
            QueueManager.sendQueue();
        }
    }

    @Subcommand("leave")
    @Description("Leaves the queue")
    public void leave(Player player) {
        QueueManager.removePlayerFromQueue(player.getName());
        player.sendMessage(ChatColor.RED + "You left the queue!");
        QueueManager.sendQueue();
    }

    @Subcommand("add")
    @CommandPermission("minecraft.command.op|warlords.queue.clear")
    @Description("Adds a player to the queue")
    public void add(Player player, @Flags("other") Player target) {
        QueueManager.addPlayerToQueue(target.getUniqueId(), false);
        player.sendMessage(QueueManager.getQueue());
        QueueManager.sendQueue();
    }

    @Subcommand("remove")
    @CommandPermission("minecraft.command.op|warlords.queue.clear")
    @Description("Removes a player from the queue")
    public void remove(Player player, Integer queuePosition) {
        if (queuePosition > QueueManager.queue.size() || queuePosition < 1) {
            player.sendMessage(ChatColor.RED + "Invalid queue number!");
            return;
        }

        QueueManager.queue.remove(queuePosition - 1);
        player.sendMessage(QueueManager.getQueue());
        QueueManager.sendQueue();
    }

    @Subcommand("clear")
    @CommandPermission("minecraft.command.op|warlords.queue.clear")
    @Description("Clears the queue")
    public void clear(Player player) {
        QueueManager.queue.clear();
        QueueManager.futureQueue.clear();
        QueueManager.sendQueue();
        player.sendMessage(ChatColor.GREEN + "Queue cleared");
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }

}
