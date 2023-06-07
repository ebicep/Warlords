package com.ebicep.warlords.commands.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import com.ebicep.jda.BotManager;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.general.TournamentStats;

import java.util.List;
import java.util.stream.Collectors;

@CommandAlias("tournament")
@CommandPermission("group.administrator")
public class TournamentCommand extends BaseCommand {

    @Subcommand("print")
    public void printStats(CommandIssuer issuer, PlayersCollections collection) {
        List<DatabasePlayer> sortedTournamentWins = DatabaseManager.CACHED_PLAYERS
                .get(collection)
                .values()
                .stream()
                .filter(databasePlayer -> databasePlayer.getTournamentStats().getCurrentTournamentStats().getPlays() > 0)
                .sorted((o1, o2) -> Integer.compare(
                        o2.getTournamentStats().getCurrentTournamentStats().getWins(),
                        o1.getTournamentStats().getCurrentTournamentStats().getWins()
                ))
                .collect(Collectors.toList());
        StringBuilder sb = new StringBuilder();
        // POS. NAME - W/L - K/A/D
        // 1. sumSmash - 5/2 - 150/10/20
        for (int i = 0; i < sortedTournamentWins.size(); i++) {
            DatabasePlayer databasePlayer = sortedTournamentWins.get(i);
            TournamentStats.DatabasePlayerTournamentStats tournamentStats = databasePlayer.getTournamentStats().getCurrentTournamentStats();
            sb.append((i + 1)).append(". ").append(databasePlayer.getName()).append(" - ")
              .append(tournamentStats.getWins()).append("/").append(tournamentStats.getLosses()).append(" - ")
              .append(tournamentStats.getKills()).append("/").append(tournamentStats.getAssists()).append("/").append(tournamentStats.getDeaths())
              .append("\n");
        }
        issuer.sendMessage(sb.toString());
        System.out.println(sb);
        Warlords.newChain()
                .async(() -> BotManager.sendDebugMessage(sb.toString()))
                .execute();
    }

}
