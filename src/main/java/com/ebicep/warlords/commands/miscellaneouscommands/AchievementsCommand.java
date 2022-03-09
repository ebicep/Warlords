package com.ebicep.warlords.commands.miscellaneouscommands;

import com.ebicep.warlords.Warlords;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AchievementsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {


        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("achievements").setExecutor(this);
    }

}
