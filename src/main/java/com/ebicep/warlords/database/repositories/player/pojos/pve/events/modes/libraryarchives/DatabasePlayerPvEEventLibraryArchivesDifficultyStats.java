package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.DatabaseGamePlayerPvEEventLibraryArchives;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.DatabaseGamePvEEventLibraryArchives;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.forgottencodex.DatabaseGamePlayerPvEEventForgottenCodex;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.forgottencodex.DatabaseGamePvEEventForgottenCodex;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.grimoiresgraveyard.DatabaseGamePlayerPvEEventGrimoiresGraveyard;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.grimoiresgraveyard.DatabaseGamePvEEventGrimoiresGraveyard;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.EventMode;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.forgottencodex.DatabasePlayerPvEEventLibraryForgottenCodexDifficultyStats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.grimoiresgraveyard.DatabasePlayerPvEEventLibraryArchivesGrimoiresGraveyardDifficultyStats;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.gameevents.libraryarchives.PlayerCodex;
import com.ebicep.warlords.pve.gameevents.libraryarchives.PlayerCodexEarnEvent;
import org.bukkit.Bukkit;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;
import java.util.stream.Stream;

public class DatabasePlayerPvEEventLibraryArchivesDifficultyStats implements MultiPvEEventLibraryArchivesStats<
        PvEEventLibraryArchivesStatsWarlordsClasses<
                DatabaseGamePvEEventLibraryArchives<DatabaseGamePlayerPvEEventLibraryArchives>,
                DatabaseGamePlayerPvEEventLibraryArchives,
                PvEEventLibraryArchivesStats<DatabaseGamePvEEventLibraryArchives<DatabaseGamePlayerPvEEventLibraryArchives>, DatabaseGamePlayerPvEEventLibraryArchives>,
                PvEEventLibraryArchivesStatsWarlordsSpecs<DatabaseGamePvEEventLibraryArchives<DatabaseGamePlayerPvEEventLibraryArchives>, DatabaseGamePlayerPvEEventLibraryArchives, PvEEventLibraryArchivesStats<DatabaseGamePvEEventLibraryArchives<DatabaseGamePlayerPvEEventLibraryArchives>, DatabaseGamePlayerPvEEventLibraryArchives>>>,
        DatabaseGamePvEEventLibraryArchives<DatabaseGamePlayerPvEEventLibraryArchives>,
        DatabaseGamePlayerPvEEventLibraryArchives,
        PvEEventLibraryArchivesStats<DatabaseGamePvEEventLibraryArchives<DatabaseGamePlayerPvEEventLibraryArchives>, DatabaseGamePlayerPvEEventLibraryArchives>,
        PvEEventLibraryArchivesStatsWarlordsSpecs<DatabaseGamePvEEventLibraryArchives<DatabaseGamePlayerPvEEventLibraryArchives>,
                DatabaseGamePlayerPvEEventLibraryArchives, PvEEventLibraryArchivesStats<DatabaseGamePvEEventLibraryArchives<DatabaseGamePlayerPvEEventLibraryArchives>, DatabaseGamePlayerPvEEventLibraryArchives>>>,
        EventMode {

    @Field("forgotten_codex_stats")
    private DatabasePlayerPvEEventLibraryForgottenCodexDifficultyStats forgottenCodexStats = new DatabasePlayerPvEEventLibraryForgottenCodexDifficultyStats();
    @Field("grimoires_graveyard_stats")
    private DatabasePlayerPvEEventLibraryArchivesGrimoiresGraveyardDifficultyStats grimoiresGraveyardStats = new DatabasePlayerPvEEventLibraryArchivesGrimoiresGraveyardDifficultyStats();
    @Field("codexes_earned")
    private Map<PlayerCodex, Integer> codexesEarned = new HashMap<>();

    @Field("event_points_spent")
    private long eventPointsSpent;
    @Field("rewards_purchased")
    private Map<String, Long> rewardsPurchased = new LinkedHashMap<>();
    @Field("completed_bounties")
    private Map<Bounty, Long> completedBounties = new HashMap<>();
    @Field("bounties_completed")
    private int bountiesCompleted = 0;
    @Field("active_bounties")
    private List<AbstractBounty> activeBounties = new ArrayList<>();

    @Override
    public long getEventPointsSpent() {
        return eventPointsSpent;
    }

    @Override
    public void addEventPointsSpent(long eventPointsSpent) {
        this.eventPointsSpent += eventPointsSpent;
    }

    @Override
    public Map<String, Long> getRewardsPurchased() {
        return rewardsPurchased;
    }

    @Override
    public int getEventPlays() {
        return forgottenCodexStats.getPlays() + grimoiresGraveyardStats.getPlays();
    }

    @Override
    public Map<Bounty, Long> getCompletedBounties() {
        return completedBounties;
    }

    @Override
    public int getBountiesCompleted() {
        return bountiesCompleted;
    }

    @Override
    public void addBountiesCompleted() {
        this.bountiesCompleted++;
    }

    @Override
    public List<AbstractBounty> getActiveEventBounties() {
        return activeBounties;
    }

    public DatabasePlayerPvEEventLibraryArchivesDifficultyStats() {
    }

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGamePvEEventLibraryArchives databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEEventLibraryArchives gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        if (databaseGame instanceof DatabaseGamePvEEventForgottenCodex gamePvEEventForgottenCodex && gamePlayer instanceof DatabaseGamePlayerPvEEventForgottenCodex gamePlayerPvEEventForgottenCodex) {
            this.forgottenCodexStats.updateStats(databasePlayer, gamePvEEventForgottenCodex, gameMode, gamePlayerPvEEventForgottenCodex, result, multiplier, playersCollection);
        } else if (databaseGame instanceof DatabaseGamePvEEventGrimoiresGraveyard gamePvEEventGrimoiresGraveyard && gamePlayer instanceof DatabaseGamePlayerPvEEventGrimoiresGraveyard gamePlayerGrimoiresGraveyard) {
            PlayerCodex codexEarned = gamePlayerGrimoiresGraveyard.getCodexEarned();
            if (codexEarned != null) {
                this.codexesEarned.merge(codexEarned, multiplier, Integer::sum);
                if (multiplier > 0) {
                    Bukkit.getPluginManager().callEvent(new PlayerCodexEarnEvent(databasePlayer.getUuid(), codexEarned));
                }
            }
            this.grimoiresGraveyardStats.updateStats(databasePlayer, gamePvEEventGrimoiresGraveyard, gameMode, gamePlayerGrimoiresGraveyard, result, multiplier, playersCollection);
        }
    }

    @Override
    public Collection<? extends PvEEventLibraryArchivesStatsWarlordsClasses<DatabaseGamePvEEventLibraryArchives<DatabaseGamePlayerPvEEventLibraryArchives>, DatabaseGamePlayerPvEEventLibraryArchives, PvEEventLibraryArchivesStats<DatabaseGamePvEEventLibraryArchives<DatabaseGamePlayerPvEEventLibraryArchives>, DatabaseGamePlayerPvEEventLibraryArchives>, PvEEventLibraryArchivesStatsWarlordsSpecs<DatabaseGamePvEEventLibraryArchives<DatabaseGamePlayerPvEEventLibraryArchives>, DatabaseGamePlayerPvEEventLibraryArchives, PvEEventLibraryArchivesStats<DatabaseGamePvEEventLibraryArchives<DatabaseGamePlayerPvEEventLibraryArchives>, DatabaseGamePlayerPvEEventLibraryArchives>>>> getStats() {
        return Stream.of(forgottenCodexStats, grimoiresGraveyardStats)
                     .flatMap(stats -> (Stream<? extends PvEEventLibraryArchivesStatsWarlordsClasses<DatabaseGamePvEEventLibraryArchives<DatabaseGamePlayerPvEEventLibraryArchives>, DatabaseGamePlayerPvEEventLibraryArchives, PvEEventLibraryArchivesStats<DatabaseGamePvEEventLibraryArchives<DatabaseGamePlayerPvEEventLibraryArchives>, DatabaseGamePlayerPvEEventLibraryArchives>, PvEEventLibraryArchivesStatsWarlordsSpecs<DatabaseGamePvEEventLibraryArchives<DatabaseGamePlayerPvEEventLibraryArchives>, DatabaseGamePlayerPvEEventLibraryArchives, PvEEventLibraryArchivesStats<DatabaseGamePvEEventLibraryArchives<DatabaseGamePlayerPvEEventLibraryArchives>, DatabaseGamePlayerPvEEventLibraryArchives>>>>) stats.getStats()
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               .stream())
                     .toList();
    }

    public DatabasePlayerPvEEventLibraryForgottenCodexDifficultyStats getForgottenCodexStats() {
        return forgottenCodexStats;
    }

    public DatabasePlayerPvEEventLibraryArchivesGrimoiresGraveyardDifficultyStats getGrimoiresGraveyardStats() {
        return grimoiresGraveyardStats;
    }

    public Map<PlayerCodex, Integer> getCodexesEarned() {
        return codexesEarned;
    }


    @Override
    public long getEventPointsCumulative() {
        return MultiPvEEventLibraryArchivesStats.super.getEventPointsCumulative();
    }
}
