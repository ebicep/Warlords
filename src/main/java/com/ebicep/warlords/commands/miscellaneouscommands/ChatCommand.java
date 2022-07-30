package com.ebicep.warlords.commands.miscellaneouscommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.permissions.PermissionHandler;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.chat.ChatChannels;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import static com.ebicep.warlords.util.chat.ChatChannels.switchChannels;

@CommandAlias("chat")
public class ChatCommand extends BaseCommand {
    public static void sendDebugMessage(Player player, String message) {
        ChatChannels.playerSendMessage(player, message, ChatChannels.DEBUG);
        //System.out.println(ChatColor.RED + "Debug" + ChatColor.DARK_GRAY + " > " + message);
    }

    public static void sendDebugMessage(WarlordsPlayer warlordsPlayer, String message) {
        if (warlordsPlayer.getEntity() instanceof Player) {
            ChatChannels.playerSendMessage((Player) warlordsPlayer.getEntity(), message, ChatChannels.DEBUG);
        }
    }

    public static void sendDebugMessage(CommandIssuer commandIssuer, String message) {
        if (commandIssuer.getIssuer() instanceof Player) {
            sendDebugMessage((Player) commandIssuer.getIssuer(), message);
        } else {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (PermissionHandler.isAdmin(onlinePlayer)) {
                    onlinePlayer.sendMessage(ChatColor.RED + "Debug" + ChatColor.DARK_GRAY + " > " + ChatColor.YELLOW + "Console: " + ChatColor.WHITE + message);
                }
            }
            commandIssuer.sendMessage(ChatColor.RED + "Debug" + ChatColor.DARK_GRAY + " > " + message);
        }
    }

    @Subcommand("all|a")
    @Description("Switch to ALL chat channel")
    public void allChat(@Conditions("otherChatChannel:target=ALL") Player player) {
        switchChannels(player, ChatChannels.ALL);
    }

    @Subcommand("party|p")
    @Description("Switch to PARTY chat channel")
    public void partyChat(@Conditions("requireParty|otherChatChannel:target=PARTY") Player player) {
        switchChannels(player, ChatChannels.PARTY);
    }

    @Subcommand("guild|g")
    @Description("Switch to GUILD chat channel")
    public void guildChat(@Conditions("requireGuild|otherChatChannel:target=GUILD") Player player) {
        switchChannels(player, ChatChannels.GUILD);
    }

    @CommandAlias("achat|ac")
    @Description("Send a message to the ALL chat channel")
    public void allChat(Player player, String message) {
        ChatChannels.playerSendMessage(player, message, ChatChannels.ALL);
    }

    @CommandAlias("pchat|pc")
    @Description("Send a message to the PARTY chat channel")
    public void partyChat(@Conditions("requireParty") Player player, String message) {
        ChatChannels.playerSendMessage(player, message, ChatChannels.PARTY);
    }

    @CommandAlias("gchat|gc")
    @Description("Send a message to the GUILD chat channel")
    public void guildChat(@Conditions("requireGuild") Player player, String message) {
        ChatChannels.playerSendMessage(player, message, ChatChannels.GUILD);
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.showHelp();
    }

}
