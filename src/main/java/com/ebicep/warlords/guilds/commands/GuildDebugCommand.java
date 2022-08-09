package com.ebicep.warlords.guilds.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.commands.miscellaneouscommands.ChatCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("gdebug")
@CommandPermission("group.adminisrator")
public class GuildDebugCommand extends BaseCommand {

    @Subcommand("experience")
    @Description("Sets the experience of the guild")
    public void setExperience(@Conditions("guild:true") Player player, GuildPlayerWrapper guildPlayerWrapper, Integer amount) {
        guildPlayerWrapper.getGuild().setExperience(amount);
        ChatCommand.sendDebugMessage(player, ChatColor.GREEN + "Set guild " + guildPlayerWrapper.getGuild().getName() + " experience to " + ChatColor.YELLOW + amount, true);
    }

    @Subcommand("coins")
    @Description("Sets the coins of the guild")
    public void setCoins(@Conditions("guild:true") Player player, GuildPlayerWrapper guildPlayerWrapper, Integer amount) {
        guildPlayerWrapper.getGuild().setCoins(amount);
        ChatCommand.sendDebugMessage(player, ChatColor.GREEN + "Set guild " + guildPlayerWrapper.getGuild().getName() + " coins to " + ChatColor.YELLOW + amount, true);
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.showHelp();
    }

}
