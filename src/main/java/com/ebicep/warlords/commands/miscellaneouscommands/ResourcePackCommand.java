package com.ebicep.warlords.commands.miscellaneouscommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("resource")
public class ResourcePackCommand extends BaseCommand {

    @Default
    @Description("Prints the resourcepack link")
    public void resource(Player player) {
        player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Download Link: https://bit.ly/3J1lGGn");
    }

}
