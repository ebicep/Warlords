package com.ebicep.warlords.guilds.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.database.repositories.timings.pojos.Timing;
import com.ebicep.warlords.guilds.GuildManager;
import com.ebicep.warlords.guilds.upgrades.GuildUpgrades;
import com.ebicep.warlords.util.chat.ChatChannels;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Comparator;

@CommandAlias("gdebug")
@CommandPermission("group.adminisrator")
public class GuildDebugCommand extends BaseCommand {
    @Subcommand("experience")
    @Description("Sets the experience of the guild")
    public void setExperience(
            @Conditions("guild:true") Player player,
            GuildPlayerWrapper guildPlayerWrapper,
            Timing timing,
            Integer amount
    ) {
        guildPlayerWrapper.getGuild().setExperience(timing, amount);
        GuildManager.queueUpdateGuild(guildPlayerWrapper.getGuild());
        ChatChannels.sendDebugMessage(player,
                                      ChatColor.GREEN + "Set guild " + guildPlayerWrapper.getGuild()
                                                                                         .getName() + " experience to " + ChatColor.YELLOW + amount,
                                      true
        );
    }

    @Subcommand("coins")
    @Description("Sets the coins of the guild")
    public void setCoins(
            @Conditions("guild:true") Player player,
            GuildPlayerWrapper guildPlayerWrapper,
            Timing timing,
            Integer amount
    ) {
        guildPlayerWrapper.getGuild().setCoins(timing, amount);
        GuildManager.queueUpdateGuild(guildPlayerWrapper.getGuild());
        ChatChannels.sendDebugMessage(player,
                                      ChatColor.GREEN + "Set guild " + guildPlayerWrapper.getGuild()
                                                                                         .getName() + " coins to " + ChatColor.YELLOW + amount,
                                      true
        );
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }

    @Subcommand("upgrades")
    public class GuildDebugUpgradeCommand extends BaseCommand {

        @Subcommand("add")
        @Description("Adds upgrades to the guild")
        public void addUpgrade(
                @Conditions("guild:true") Player player,
                GuildPlayerWrapper guildPlayerWrapper,
                GuildUpgrades upgrade,
                @Conditions("limits:min=1,max=9") Integer tier
        ) {
            guildPlayerWrapper.getGuild().getUpgrades().add(upgrade.createUpgrade(tier));
            GuildManager.queueUpdateGuild(guildPlayerWrapper.getGuild());
            ChatChannels.sendDebugMessage(player,
                                          ChatColor.GREEN + "Added upgrade " + ChatColor.YELLOW + upgrade.name + " (" + tier + ") " + ChatColor.GREEN + "to guild",
                                          true
            );
        }

        @Subcommand("clear")
        @Description("Clears upgrades of the guild")
        public void clear(
                @Conditions("guild:true") Player player,
                GuildPlayerWrapper guildPlayerWrapper
        ) {
            guildPlayerWrapper.getGuild().getUpgrades().clear();
            ChatChannels.sendDebugMessage(player,
                                          ChatColor.GREEN + "Cleared upgrades of guild",
                                          true
            );
        }

    }

}
