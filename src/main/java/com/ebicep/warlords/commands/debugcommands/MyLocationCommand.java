package com.ebicep.warlords.commands.debugcommands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.util.java.NumberFormat;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MyLocationCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!sender.hasPermission("warlords.game.location")) {
            sender.sendMessage("§cYou do not have permission to do that.");
            return true;
        }

        Location location = ((Player) sender).getLocation();
        String locationString = NumberFormat.formatOptionalTenths(roundToHalf(location.getX())) + ", " + NumberFormat.formatOptionalTenths(roundToHalf(location.getY())) + ", " + NumberFormat.formatOptionalTenths(roundToHalf(location.getZ()));
        TextComponent text = new TextComponent(ChatColor.AQUA.toString() + ChatColor.BOLD + locationString);
        text.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, locationString));
        ((Player) sender).spigot().sendMessage(text);

        return true;
    }

    public static double roundToHalf(double d) {
        return Math.round(d * 2) / 2.0;
    }

    public void register(Warlords instance) {
        instance.getCommand("mylocation").setExecutor(this);
    }
}
