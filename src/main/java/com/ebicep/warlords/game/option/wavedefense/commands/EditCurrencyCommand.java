package com.ebicep.warlords.game.option.wavedefense.commands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.BaseCommand;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class EditCurrencyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        WarlordsEntity we = BaseCommand.requireWarlordsPlayer(sender);
        if (we != null) {
            if (we.getGame() != null && we.getGame().getGameMode().equals(GameMode.WAVE_DEFENSE)) {
                if (args[0] != null || args[1] != null) {
                    int amount = Integer.parseInt(args[1]);
                    switch (args[0]) {
                        case "add":
                            we.addCurrency(amount);
                            we.sendMessage(ChatColor.AQUA + "You gained " + amount + " currency");
                            break;
                        case "remove":
                            we.subtractCurrency(amount);
                            we.sendMessage(ChatColor.AQUA + "You lost " + amount + " currency");
                            break;
                        case "set":
                            we.setCurrency(amount);
                            we.sendMessage(ChatColor.AQUA + "You set your currency to " + amount);
                            break;
                        default:
                            we.sendMessage(ChatColor.RED + "Invalid use case. Usage: /currency [add, set, remove] [amount]");
                            break;
                    }
                } else {
                    we.sendMessage(ChatColor.RED + "Invalid use case. Usage: /currency [add, set, remove] [amount]");
                    return true;
                }
            } else {
                we.sendMessage(ChatColor.RED + "You are not in a Wave Defense gamemode instance!");
            }
        }

        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("currency").setExecutor(this);
    }

}