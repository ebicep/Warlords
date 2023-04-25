package com.ebicep.warlords.guilds.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.database.repositories.timings.pojos.Timing;
import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildManager;
import com.ebicep.warlords.guilds.GuildTag;
import com.ebicep.warlords.guilds.menu.GuildMenu;
import com.ebicep.warlords.guilds.upgrades.temporary.GuildUpgradesTemporary;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Comparator;

@CommandAlias("gdebug")
@CommandPermission("group.administrator")
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
                Component.text("Set guild " + guildPlayerWrapper.getGuild().getName() + " experience to ", NamedTextColor.GREEN)
                         .append(Component.text(amount, NamedTextColor.YELLOW))
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
        guildPlayerWrapper.getGuild().setCurrentCoins(amount);
        GuildManager.queueUpdateGuild(guildPlayerWrapper.getGuild());
        ChatChannels.sendDebugMessage(player,
                Component.text("Set guild " + guildPlayerWrapper.getGuild().getName() + " current coins to ", NamedTextColor.GREEN)
                         .append(Component.text(amount, NamedTextColor.YELLOW))
        );
    }

    @Subcommand("tag")
    @Description("Gives guild tag")
    public void tag(
            @Conditions("guild:true") Player player,
            GuildPlayerWrapper guildPlayerWrapper,
            String tagName,
            ChatColor nameColor,
            ChatColor bracketColor
    ) {
        Guild guild = guildPlayerWrapper.getGuild();
        GuildTag tag = new GuildTag(tagName, nameColor.toString(), bracketColor.toString());
        guild.setTag(tag);
        GuildManager.queueUpdateGuild(guild);
        ChatChannels.sendDebugMessage(player, Component.text("Set guild " + guild.getName() + " tag to " + guild.getTag().getTag(false), NamedTextColor.GREEN)
        );
    }

    @Subcommand("getlog")
    @Description("Gets audit log of a guild")
    public void getLog(Player player, String guildName) {
        GuildManager.getGuildFromName(guildName).ifPresent(guild -> guild.printAuditLog(player, Integer.MAX_VALUE));
    }

    @Subcommand("getlogpaged")
    @Description("Gets audit log of a guild at page")
    public void getLogPaged(Player player, Integer page, String guildName) {
        GuildManager.getGuildFromName(guildName).ifPresent(guild -> guild.printAuditLog(player, page));
    }

    @Subcommand("openmenu")
    @Description("Opens guild menu of any guild")
    public void openMenu(Player player, String guildName) {
        GuildManager.getGuildFromName(guildName).ifPresent(guild -> GuildMenu.openGuildMenu(guild, player, 1));
    }

    @HelpCommand
    public void help(
            CommandIssuer issuer, CommandHelp help
    ) {
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
                GuildUpgradesTemporary upgrade,
                @Conditions("limits:min=1,max=9") Integer tier
        ) {
            guildPlayerWrapper.getGuild().addUpgrade(upgrade.createUpgrade(tier));
            GuildManager.queueUpdateGuild(guildPlayerWrapper.getGuild());
            ChatChannels.sendDebugMessage(player,
                    Component.text("Added upgrade ", NamedTextColor.GREEN)
                             .append(Component.text(upgrade.name + " (" + tier + ") ", NamedTextColor.YELLOW))
                             .append(Component.text("to guild"))
            );
        }

        @Subcommand("clear")
        @Description("Clears upgrades of the guild")
        public void clear(
                @Conditions("guild:true") Player player,
                GuildPlayerWrapper guildPlayerWrapper
        ) {
            guildPlayerWrapper.getGuild().getUpgrades().clear();
            ChatChannels.sendDebugMessage(player, Component.text("Cleared upgrades of guild", NamedTextColor.GREEN)
            );
        }

    }

}
