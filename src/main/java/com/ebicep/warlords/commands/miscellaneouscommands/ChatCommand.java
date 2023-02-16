package com.ebicep.warlords.commands.miscellaneouscommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.guilds.commands.GuildPlayerWrapper;
import com.ebicep.warlords.util.chat.ChatChannels;
import org.bukkit.entity.Player;

import java.util.Comparator;

import static com.ebicep.warlords.util.chat.ChatChannels.switchChannels;

@CommandAlias("chat")
public class ChatCommand extends BaseCommand {

    @Subcommand("all|a")
    @Description("Switch to ALL chat channel")
    public void allChat(@Conditions("otherChatChannel:target=ALL") Player player) {
        switchChannels(player, ChatChannels.ALL);
    }

    @Subcommand("party|p")
    @Description("Switch to PARTY chat channel")
    public void partyChat(@Conditions("party:true|otherChatChannel:target=PARTY") Player player) {
        switchChannels(player, ChatChannels.PARTY);
    }

    @Subcommand("guild|g")
    @Description("Switch to GUILD chat channel")
    public void guildChat(@Conditions("guild:true|otherChatChannel:target=GUILD") Player player) {
        switchChannels(player, ChatChannels.GUILD);
    }

    @Subcommand("guildofficer|go")
    @Description("Switch to GUILD OFFICER chat channel")
    public void guildOfficerChat(
            @Conditions("guild:true|otherChatChannel:target=GUILD_OFFICER") Player player,
            @Conditions("requirePerm:perm=OFFICER_CHAT") GuildPlayerWrapper guildPlayerWrapper
    ) {
        switchChannels(player, ChatChannels.GUILD_OFFICER);
    }

    @CommandAlias("achat|ac")
    @Description("Send a message to the ALL chat channel")
    public void allChat(Player player, String message) {
        ChatChannels.playerSendMessage(player, ChatChannels.ALL, message);
    }

    @CommandAlias("pchat|pc")
    @Description("Send a message to the PARTY chat channel")
    public void partyChat(@Conditions("party:true") Player player, String message) {
        ChatChannels.playerSendMessage(player, ChatChannels.PARTY, message);
    }

    @CommandAlias("gchat|gc")
    @Description("Send a message to the GUILD chat channel")
    public void guildChat(@Conditions("guild:true") Player player, String message) {
        ChatChannels.playerSendMessage(player, ChatChannels.GUILD, message);
    }

    @CommandAlias("gochat|goc")
    @Description("Send a message to the GUILD OFFICER chat channel")
    public void guildOfficerChat(
            @Conditions("guild:true") Player player, @Conditions("requirePerm:perm=OFFICER_CHAT") GuildPlayerWrapper guildPlayerWrapper,
            String message
    ) {
        ChatChannels.playerSendMessage(player, ChatChannels.GUILD_OFFICER, message);
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }

}
