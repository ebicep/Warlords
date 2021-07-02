package com.ebicep.warlords.commands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.state.TimerDebugAble;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DebugCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!sender.isOp()) {
            sender.sendMessage("§cYou do not have permission to do that.");
            return true;
        }
        Game game = Warlords.game; // In the future allow the user to select a game player
        if (args.length < 1) {
            sender.sendMessage("§cYou need to pass an argument, valid arguments: [timer]");
            return true;
        }
        switch (args[0]) {
            case "timer":
                if (!(game.getState() instanceof TimerDebugAble)) {
                    sender.sendMessage("This gamestate cannot be manipulated by the timer debug option");
                    return true;
                }
                TimerDebugAble timerDebugAble = (TimerDebugAble) game.getState();
                if (args.length < 2) {
                    sender.sendMessage("§cTimer required 2 or more arguments, valid arguments: [skip, reset]");
                    return true;
                }
                switch(args[1]) {
                    case "reset":
                        timerDebugAble.resetTimer();
                        sender.sendMessage(ChatColor.GREEN + "Timer has been reset!");
                        return true;
                    case "skip":
                        timerDebugAble.skipTimer();
                        sender.sendMessage(ChatColor.GREEN + "Timer has been skipped!");
                        return true;
                    default:
                        sender.sendMessage("Invalid option!");
                        return true;
                }

            default:
                sender.sendMessage("Invalid option!");
                return true;
        }
    }

    public void register(Warlords instance) {
        instance.getCommand("wldebug").setExecutor(this);
        //instance.getCommand("wldebug").setTabCompleter(this);
    }
}
