package com.ebicep.warlords.database.leaderboards.stats;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.database.leaderboards.PlayerLeaderboardInfo;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.Comparator;

@CommandAlias("leaderboard|lb")
@CommandPermission("warlords.leaderboard.interaction")
public class StatsLeaderboardCommand extends BaseCommand {

    @Subcommand("toggle")
    public void toggle(CommandIssuer issuer) {
        StatsLeaderboardManager.enabled = !StatsLeaderboardManager.enabled;
        StatsLeaderboardManager.addHologramLeaderboards(false);
        if (StatsLeaderboardManager.enabled) {
            ChatChannels.sendDebugMessage(issuer, Component.text("Leaderboards enabled", NamedTextColor.GREEN));
        } else {
            ChatChannels.sendDebugMessage(issuer, Component.text("Leaderboards disabled", NamedTextColor.GREEN));
        }
    }

    @Subcommand("forcereload")
    public void forceReload(CommandIssuer issuer) {
        StatsLeaderboardManager.addHologramLeaderboards(false);
        ChatChannels.sendDebugMessage(issuer, Component.text("Leaderboards reloaded", NamedTextColor.GREEN));
    }

    @Subcommand("reload")
    public void reload(CommandIssuer issuer, @Optional PlayersCollections collection) {
        if (collection == null) {
            for (PlayersCollections activeCollection : PlayersCollections.ACTIVE_LEADERBOARD_COLLECTIONS) {
                StatsLeaderboardManager.resetLeaderboards(activeCollection, null);
            }
            ChatChannels.sendDebugMessage(issuer, Component.text("All leaderboards reloaded", NamedTextColor.GREEN));
        } else {
            StatsLeaderboardManager.resetLeaderboards(collection, null);
            ChatChannels.sendDebugMessage(issuer, Component.text(collection.name + " leaderboards reloaded", NamedTextColor.GREEN));
        }
    }

    @Subcommand("refresh")
    public void refresh(CommandIssuer issuer) {
        StatsLeaderboardManager.setLeaderboardHologramVisibilityToAll();
        ChatChannels.sendDebugMessage(issuer, Component.text("Refreshed visibility for all players", NamedTextColor.GREEN));
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
