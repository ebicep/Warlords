package com.ebicep.warlords.commands.miscellaneouscommands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.achievements.AchievementsMenu;
import com.ebicep.warlords.commands.BaseCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AchievementsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {


        Player player = BaseCommand.requirePlayer(sender);

        if (player == null) {
            return true;
        }

        //AchievementsMenu.openAchievementsMenu(player);

        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("achievements").setExecutor(this);
    }

}
