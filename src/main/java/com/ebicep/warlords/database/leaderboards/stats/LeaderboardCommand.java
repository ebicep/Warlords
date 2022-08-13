package com.ebicep.warlords.database.leaderboards.stats;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Subcommand;
import com.ebicep.warlords.commands.miscellaneouscommands.ChatCommand;
import com.ebicep.warlords.database.leaderboards.PlayerLeaderboardInfo;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("leaderboard|lb")
@CommandPermission("warlords.leaderboard.interaction")
public class LeaderboardCommand extends BaseCommand {

    @Subcommand("toggle")
    public void toggle(CommandIssuer issuer) {
        LeaderboardManager.enabled = !LeaderboardManager.enabled;
        LeaderboardManager.addHologramLeaderboards(false);
        if (LeaderboardManager.enabled) {
            ChatCommand.sendDebugMessage(issuer, ChatColor.GREEN + "Leaderboards enabled", true);
        } else {
            ChatCommand.sendDebugMessage(issuer, ChatColor.RED + "Leaderboards disabled", true);
        }
    }

    @Subcommand("reload")
    public void reload(CommandIssuer issuer) {
        LeaderboardManager.addHologramLeaderboards(false);
        ChatCommand.sendDebugMessage(issuer, ChatColor.GREEN + "Leaderboards reloaded", true);
    }

    @Subcommand("refresh")
    public void refresh(CommandIssuer issuer) {
        LeaderboardManager.setLeaderboardHologramVisibilityToAll();
        ChatCommand.sendDebugMessage(issuer, ChatColor.GREEN + "Refreshed visibility for all players", true);
    }

    @Subcommand("page")
    public void page(Player player) {
        PlayerLeaderboardInfo playerLeaderboardInfo = LeaderboardManager.PLAYER_LEADERBOARD_INFOS.get(player.getUniqueId());
        playerLeaderboardInfo.setPage(playerLeaderboardInfo.getPageAfter());
        LeaderboardManager.setLeaderboardHologramVisibility(player);
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.showHelp();
    }

}
