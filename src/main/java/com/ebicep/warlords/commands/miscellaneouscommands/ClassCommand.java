package com.ebicep.warlords.commands.miscellaneouscommands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.BaseCommand;
import com.ebicep.warlords.player.PlayerSettings;
import com.ebicep.warlords.player.Specializations;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Locale;

public class ClassCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!sender.hasPermission("warlords.game.changeclass")) {
            return true;
        }

        Player player = BaseCommand.requirePlayerOutsideGame(sender);
        if (player != null) {
            PlayerSettings settings = Warlords.getPlayerSettings(player.getUniqueId());
            if (args.length != 0) {
                try {
                    Specializations selectedSpec = Specializations.valueOf(args[0].toUpperCase(Locale.ROOT));
                    settings.setSelectedSpec(selectedSpec);
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(ChatColor.RED + args[0] + " was not found, valid classes: " + Arrays.toString(Specializations.values()));
                    return true;
                }
            }

            Specializations selected = settings.getSelectedSpec();
            player.sendMessage(ChatColor.BLUE + "Your selected spec: §7" + selected);
        }
        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("class").setExecutor(this);
        //instance.getCommand("class").setTabCompleter(this);
    }
}
