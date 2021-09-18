package com.ebicep.warlords.commands.miscellaneouscommands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.BaseCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.ebicep.warlords.menu.GameMenu.openMainMenu;

public class MenuCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        Player player = BaseCommand.requirePlayerOutsideGame(sender);
        if (player != null) {
            openMainMenu(player);
        }

        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("menu").setExecutor(this);
    }
}
