package com.ebicep.warlords.commands.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.ebicep.warlords.util.java.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@CommandAlias("mylocation")
@CommandPermission("warlords.game.location")
public class MyLocationCommand extends BaseCommand {

    @Default
    @Description("Prints your current location, click to copy to clipboard")
    public void myLocation(Player player) {
        Location location = player.getLocation();
        String locationString = NumberFormat.formatOptionalTenths(roundToHalf(location.getX())) + ", " +
                NumberFormat.formatOptionalTenths(roundToHalf(location.getY())) + ", " +
                NumberFormat.formatOptionalTenths(roundToHalf(location.getZ()));
        player.sendMessage(Component.text(locationString, NamedTextColor.AQUA, TextDecoration.BOLD)
                                    .clickEvent(ClickEvent.copyToClipboard(locationString)));

    }

    public static double roundToHalf(double d) {
        return Math.round(d * 2) / 2.0;
    }

}
