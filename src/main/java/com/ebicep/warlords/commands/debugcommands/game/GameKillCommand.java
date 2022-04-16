package com.ebicep.warlords.commands.debugcommands.game;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.GameManager.GameHolder;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.Collection;

public class GameKillCommand extends GameTargetCommand implements TabExecutor {

    @Override
    protected void doAction(CommandSender sender, Collection<GameHolder> gameInstances) {
        sender.sendMessage(ChatColor.RED + "DEV:" + ChatColor.GRAY + " Requesting engine to kill games...");
        if (gameInstances.isEmpty()) {
            sender.sendMessage(ChatColor.GRAY + "- " + ChatColor.RED + "No valid targets found!");
            return;
        }
        for (GameHolder holder : gameInstances) {
            if (holder.getGame() == null) {
                sender.sendMessage(ChatColor.GRAY + "- " + holder.getName() + ": " + ChatColor.RED + "The game is not active now");
                continue;
            }
            holder.forceEndGame();
            sender.sendMessage(ChatColor.GRAY + "- " + holder.getName() + ": " + ChatColor.RED + "Terminated");
        }
    }

    public void register(Warlords instance) {
        instance.getCommand("killgame").setExecutor(this);
        instance.getCommand("killgame").setTabCompleter(this);
    }
}
