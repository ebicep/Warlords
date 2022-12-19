package com.ebicep.warlords.commands.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.ebicep.warlords.util.java.NumberFormat;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@CommandAlias("mylocation")
@CommandPermission("warlords.game.location")
public class MyLocationCommand extends BaseCommand {

    public static double roundToHalf(double d) {
        return Math.round(d * 2) / 2.0;
    }

    @Default
    @Description("Prints your current location, clickable to copy to clipboard")
    public void myLocation(Player player) {
        Location location = player.getLocation();
        String locationString = NumberFormat.formatOptionalTenths(roundToHalf(location.getX())) + ", " + NumberFormat.formatOptionalTenths(roundToHalf(location.getY())) + ", " + NumberFormat.formatOptionalTenths(roundToHalf(location.getZ()));
        TextComponent text = new TextComponent(ChatColor.AQUA.toString() + ChatColor.BOLD + locationString);
        text.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, locationString));
        player.spigot().sendMessage(text);

    }

}
