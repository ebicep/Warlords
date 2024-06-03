package com.ebicep.jda.queuesystem;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
        if (QueueManager.QUEUE.contains(player.getUniqueId())) {
            player.sendMessage(Component.text("You are already in the queue!", NamedTextColor.RED));
        } else {
            QueueManager.addPlayerToQueue(player.getName(), false);
            QueueManager.removePlayerFromFutureQueue(player.getName());
            player.sendMessage(Component.text("You are now #" + QueueManager.QUEUE.size() + " in queue!", NamedTextColor.GREEN));
            QueueManager.sendQueue();
        }
    }

    @Subcommand("leave")
    @Description("Leaves the queue")
    public void leave(Player player) {
        QueueManager.removePlayerFromQueue(player.getName());
        player.sendMessage(Component.text("You left the queue!", NamedTextColor.RED));
        QueueManager.sendQueue();
    }

    @Subcommand("add")
    @CommandPermission("warlords.queue.clear")
    @Description("Adds a player to the queue")
    public void add(Player player, @Flags("other") Player target) {
        QueueManager.addPlayerToQueue(target.getUniqueId(), false);
        player.sendMessage(QueueManager.getQueue());
        QueueManager.sendQueue();
    }

    @Subcommand("remove")
    @CommandPermission("warlords.queue.clear")
    @Description("Removes a player from the queue")
    public void remove(Player player, Integer queuePosition) {
        if (queuePosition > QueueManager.QUEUE.size() || queuePosition < 1) {
            player.sendMessage(Component.text("Invalid queue number!", NamedTextColor.RED));
            return;
        }

        QueueManager.QUEUE.remove(queuePosition - 1);
        player.sendMessage(QueueManager.getQueue());
        QueueManager.sendQueue();
    }

    @Subcommand("clear")
    @CommandPermission("warlords.queue.clear")
    @Description("Clears the queue")
    public void clear(Player player) {
        QueueManager.QUEUE.clear();
        QueueManager.FUTURE_QUEUE.clear();
        QueueManager.sendQueue();
        player.sendMessage(Component.text("Queue cleared", NamedTextColor.GREEN));
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }

}
