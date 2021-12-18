package com.ebicep.warlords.commands.debugcommands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.BaseCommand;
import com.ebicep.warlords.player.WarlordsPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RecordAverageDamage implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        WarlordsPlayer warlordsPlayer = BaseCommand.requireWarlordsPlayer(sender);

        if (!sender.hasPermission("warlords.game.recordaverage")) {
            sender.sendMessage("§cYou do not have permission to do that.");
            return true;
        }

        if(warlordsPlayer != null) {
            warlordsPlayer.sendMessage(ChatColor.GREEN + "Average Damage Taken = " + ChatColor.RED + warlordsPlayer.getRecordDamage().stream()
                    .mapToDouble(Float::floatValue)
                    .average()
                    .orElse(Double.NaN));
            warlordsPlayer.getRecordDamage().clear();
        }
        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("recordaveragedamage").setExecutor(this);
    }

}
