package com.ebicep.warlords.commands;

import com.ebicep.warlords.Warlords;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class PollCommand implements CommandExecutor {

    public static final List<BukkitRunnable> polls = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage("§cYou do not have permission to do that.");
            return true;
        }
        if(!polls.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "There is already an ongoing poll");
            return true;
        }

        String input =  args[0];
        int numberOfSlashes = (int) input.chars().filter(ch -> ch == '/').count();

        if(numberOfSlashes == 1) {
            sender.sendMessage(ChatColor.RED + "You must have more than 1 answer");
            return true;
        } else {
            String[] pollOptions = input.split("/");
            String question = pollOptions[0];
            polls.add(new BukkitRunnable() {

                @Override
                public void run() {

                }
            });
        }

        Bukkit.getOnlinePlayers()
                .stream()
                .filter(player -> !Warlords.hasPlayer(player.getUniqueId()))
                .forEach(p -> {

        });

        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("poll").setExecutor(this);
    }

}
