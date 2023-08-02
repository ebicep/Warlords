package com.ebicep.warlords.commands.miscellaneouscommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

@CommandAlias("resource")
public class ResourcePackCommand extends BaseCommand {

    @Default
    @Description("Prints the resourcepack link")
    public void resource(Player player) {
        player.sendMessage(Component.text("Download Link: https://bit.ly/3DFZTmI", NamedTextColor.GREEN, TextDecoration.BOLD));
    }

}
