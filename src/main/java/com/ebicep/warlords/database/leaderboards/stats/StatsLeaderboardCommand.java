package com.ebicep.warlords.database.leaderboards.stats;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.database.leaderboards.PlayerLeaderboardInfo;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.util.chat.ChatChannels;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Comparator;

@CommandAlias("leaderboard|lb")
@CommandPermission("minecraft.command.op|warlords.leaderboard.interaction")
public class StatsLeaderboardCommand extends BaseCommand {

    @Subcommand("toggle")
    public void toggle(CommandIssuer issuer) {
        StatsLeaderboardManager.enabled = !StatsLeaderboardManager.enabled;
        StatsLeaderboardManager.addHologramLeaderboards(false);
        if (StatsLeaderboardManager.enabled) {
            ChatChannels.sendDebugMessage(issuer, ChatColor.GREEN + "Leaderboards enabled", true);
        } else {
            ChatChannels.sendDebugMessage(issuer, ChatColor.RED + "Leaderboards disabled", true);
        }
    }

    @Subcommand("forcereload")
    public void forceReload(CommandIssuer issuer) {
        StatsLeaderboardManager.addHologramLeaderboards(false);
        ChatChannels.sendDebugMessage(issuer, ChatColor.GREEN + "Leaderboards reloaded", true);
    }

    @Subcommand("reload")
    public void reload(CommandIssuer issuer, @Optional PlayersCollections collection) {
        if (collection == null) {
            for (PlayersCollections activeCollection : PlayersCollections.ACTIVE_COLLECTIONS) {
                StatsLeaderboardManager.reloadLeaderboardsFromCache(activeCollection, false);
            }
            ChatChannels.sendDebugMessage(issuer, ChatColor.GREEN + "All leaderboards reloaded", true);
        } else {
            StatsLeaderboardManager.reloadLeaderboardsFromCache(collection, false);
            ChatChannels.sendDebugMessage(issuer, ChatColor.GREEN + collection.name + " leaderboards reloaded", true);
        }
    }

    @Subcommand("refresh")
    public void refresh(CommandIssuer issuer) {
        StatsLeaderboardManager.setLeaderboardHologramVisibilityToAll();
        ChatChannels.sendDebugMessage(issuer, ChatColor.GREEN + "Refreshed visibility for all players", true);
    }

    @Subcommand("page")
    public void page(Player player) {
        PlayerLeaderboardInfo playerLeaderboardInfo = StatsLeaderboardManager.PLAYER_LEADERBOARD_INFOS.get(player.getUniqueId());
        playerLeaderboardInfo.setPage(playerLeaderboardInfo.getPageAfter());
        StatsLeaderboardManager.setLeaderboardHologramVisibility(player);
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }

}
