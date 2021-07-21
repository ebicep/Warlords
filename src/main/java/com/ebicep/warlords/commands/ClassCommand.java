package com.ebicep.warlords.commands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.maps.GameMap;
import com.ebicep.warlords.player.Classes;
import com.ebicep.warlords.player.PlayerSettings;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ClassCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
            Player player = BaseCommand.requirePlayerOutsideGame(sender);
            if (player != null) {
                PlayerSettings settings = Warlords.getPlayerSettings(player.getUniqueId());
                if (args.length != 0) {
                    try {
                        Classes selectedClass = Classes.valueOf(args[0].toUpperCase(Locale.ROOT));
                        settings.selectedClass(selectedClass);
                    } catch (IllegalArgumentException e) {
                        sender.sendMessage(ChatColor.RED + args[0] + " was not found, valid classes: " + Arrays.toString(Classes.values()));
                        return true;
                    }
                }

                Classes selected = settings.selectedClass();
                player.sendMessage(ChatColor.BLUE + "Your selected class: §7" + selected);
            }
        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("class").setExecutor(this);
        //instance.getCommand("class").setTabCompleter(this);
    }
}
