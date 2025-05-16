package com.ebicep.warlords.commands.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;
import java.util.Set;
import java.util.UUID;

@CommandAlias("build")
@CommandPermission("group.builder")
public class BuilderCommand extends BaseCommand {

    @Subcommand("gamemode|gm")
    @Description("Sets your gamemode")
    public void gamemode(@Conditions("outsideGame") Player player, GameMode gameMode) {
        player.setGameMode(gameMode);
        ChatChannels.sendDebugMessage(player, Component.text("Set your gamemode to " + gameMode, NamedTextColor.GREEN));
    }

    @Subcommand("barrier")
    @Description("Give yourself a barrier block")
    public void barrier(@Conditions("outsideGame") Player player) {
        player.getInventory().addItem(new ItemStack(Material.BARRIER));
        ChatChannels.sendDebugMessage(player, Component.text("Gave yourself a barrier block", NamedTextColor.GREEN));
    }

    @Subcommand("togglebuilding")
    @Description("Toggles if you can break blocks")
    public void toggleBuilding(@Conditions("outsideGame") Player player) {
        Set<UUID> interactCancel = AdminCommand.BYPASS_INTERACT_CANCEL;
        if (interactCancel.contains(player.getUniqueId())) {
            interactCancel.remove(player.getUniqueId());
            ChatChannels.sendDebugMessage(player, Component.text("Enabled Building", NamedTextColor.GREEN));
        } else {
            interactCancel.add(player.getUniqueId());
            ChatChannels.sendDebugMessage(player, Component.text("Disabled Building", NamedTextColor.GREEN));
        }
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }

}
