package com.ebicep.warlords.database.repositories.player.pojos.pve.events;

import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.database.repositories.events.pojos.GameEvents;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePlayerPvEEvent;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePvEEvent;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.DatabaseGamePlayerPvEEventBoltaro;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.DatabaseGamePvEEventBoltaro;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.DatabaseGamePlayerPvEEventGardenOfHesperides;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.DatabaseGamePvEEventGardenOfHesperides;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.illumina.DatabaseGamePlayerPvEEventIllumina;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.illumina.DatabaseGamePvEEventIllumina;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.DatabaseGamePlayerPvEEventLibraryArchives;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.DatabaseGamePvEEventLibraryArchives;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.mithra.DatabaseGamePlayerPvEEventMithra;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.mithra.DatabaseGamePvEEventMithra;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.narmer.DatabaseGamePlayerPvEEventNarmer;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.narmer.DatabaseGamePvEEventNarmer;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.DatabasePlayerPvEEventBoltaroDifficultyStats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.DatabasePlayerPvEEventBoltaroStats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.DatabasePlayerPvEEventGardenOfHesperidesDifficultyStats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.DatabasePlayerPvEEventGardenOfHesperidesStats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina.DatabasePlayerPvEEventIlluminaDifficultyStats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina.DatabasePlayerPvEEventIlluminaStats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.DatabasePlayerPvEEventLibraryArchivesDifficultyStats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.DatabasePlayerPvEEventLibraryArchivesStats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.DatabasePlayerPvEEventMithraDifficultyStats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.DatabasePlayerPvEEventMithraStats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.DatabasePlayerPvEEventNarmerDifficultyStats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.DatabasePlayerPvEEventNarmerStats;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildManager;
import com.ebicep.warlords.guilds.GuildPlayer;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.Pair;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

public class DatabasePlayerPvEEventStats implements MultiPvEEventStats<
        PvEEventStatsWarlordsClasses<
                DatabaseGamePvEEvent<DatabaseGamePlayerPvEEvent>,
                DatabaseGamePlayerPvEEvent,
                PvEEventStats<DatabaseGamePvEEvent<DatabaseGamePlayerPvEEvent>, DatabaseGamePlayerPvEEvent>,
                PvEEventStatsWarlordsSpecs<DatabaseGamePvEEvent<DatabaseGamePlayerPvEEvent>, DatabaseGamePlayerPvEEvent,
                        PvEEventStats<DatabaseGamePvEEvent<DatabaseGamePlayerPvEEvent>,
                                DatabaseGamePlayerPvEEvent>>>,
        DatabaseGamePvEEvent<DatabaseGamePlayerPvEEvent>,
        DatabaseGamePlayerPvEEvent,
        PvEEventStats<DatabaseGamePvEEvent<DatabaseGamePlayerPvEEvent>, DatabaseGamePlayerPvEEvent>,
        PvEEventStatsWarlordsSpecs<DatabaseGamePvEEvent<DatabaseGamePlayerPvEEvent>, DatabaseGamePlayerPvEEvent,
                PvEEventStats<DatabaseGamePvEEvent<DatabaseGamePlayerPvEEvent>,
                        DatabaseGamePlayerPvEEvent>>> {

    @Field("boltaro")
    private DatabasePlayerPvEEventBoltaroStats boltaroStats = new DatabasePlayerPvEEventBoltaroStats();
    @Field("narmer")
    private DatabasePlayerPvEEventNarmerStats narmerStats = new DatabasePlayerPvEEventNarmerStats();
    @Field("mithra")
    private DatabasePlayerPvEEventMithraStats mithraStats = new DatabasePlayerPvEEventMithraStats();
    @Field("illumina")
    private DatabasePlayerPvEEventIlluminaStats illuminaStats = new DatabasePlayerPvEEventIlluminaStats();
    @Field("garden_of_hesperides")
    private DatabasePlayerPvEEventGardenOfHesperidesStats gardenOfHesperidesStats = new DatabasePlayerPvEEventGardenOfHesperidesStats();
    @Field("library_archives")
    private DatabasePlayerPvEEventLibraryArchivesStats libraryArchivesStats = new DatabasePlayerPvEEventLibraryArchivesStats();

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGamePvEEvent databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEEvent gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        DatabaseGameEvent currentGameEvent = DatabaseGameEvent.currentGameEvent;
        if (currentGameEvent != null) {
            GameEvents event = currentGameEvent.getEvent();
            if (databaseGame instanceof DatabaseGamePvEEventBoltaro boltaroGame && gamePlayer instanceof DatabaseGamePlayerPvEEventBoltaro boltaroPlayer) {
                boltaroStats.updateStats(databasePlayer, boltaroGame, gameMode, boltaroPlayer, result, multiplier, playersCollection);
            } else if (databaseGame instanceof DatabaseGamePvEEventNarmer narmerGame && gamePlayer instanceof DatabaseGamePlayerPvEEventNarmer narmerPlayer) {
                narmerStats.updateStats(databasePlayer, narmerGame, gameMode, narmerPlayer, result, multiplier, playersCollection);
            } else if (databaseGame instanceof DatabaseGamePvEEventMithra mithraGame && gamePlayer instanceof DatabaseGamePlayerPvEEventMithra mithraPlayer) {
                mithraStats.updateStats(databasePlayer, mithraGame, gameMode, mithraPlayer, result, multiplier, playersCollection);
            } else if (databaseGame instanceof DatabaseGamePvEEventIllumina illuminaGame && gamePlayer instanceof DatabaseGamePlayerPvEEventIllumina illuminaPlayer) {
                illuminaStats.updateStats(databasePlayer, illuminaGame, gameMode, illuminaPlayer, result, multiplier, playersCollection);
            } else if (databaseGame instanceof DatabaseGamePvEEventGardenOfHesperides gardenOfHesperidesGame && gamePlayer instanceof DatabaseGamePlayerPvEEventGardenOfHesperides gardenOfHesperidesPlayer) {
                gardenOfHesperidesStats.updateStats(databasePlayer, gardenOfHesperidesGame, gameMode, gardenOfHesperidesPlayer, result, multiplier, playersCollection);
            } else if (databaseGame instanceof DatabaseGamePvEEventLibraryArchives libraryArchivesGame && gamePlayer instanceof DatabaseGamePlayerPvEEventLibraryArchives libraryArchivesPlayer) {
                libraryArchivesStats.updateStats(databasePlayer, libraryArchivesGame, gameMode, libraryArchivesPlayer, result, multiplier, playersCollection);
            } else {
                ChatUtils.MessageType.GAME_SERVICE.sendErrorMessage("Invalid game or player type");
                return;
            }
            //GUILDS
            Pair<Guild, GuildPlayer> guildGuildPlayerPair = GuildManager.getGuildAndGuildPlayerFromPlayer(gamePlayer.getUuid());
            if (playersCollection == PlayersCollections.LIFETIME && guildGuildPlayerPair != null) {
                Guild guild = guildGuildPlayerPair.getA();
                GuildPlayer guildPlayer = guildGuildPlayerPair.getB();

                long points = Math.min(gamePlayer.getPoints(), databaseGame.getPointLimit()) * multiplier;
                guild.addEventPoints(event, currentGameEvent.getStartDateSecond(), points * multiplier);
                guildPlayer.addEventPoints(event, currentGameEvent.getStartDateSecond(), points * multiplier);
                guild.queueUpdate();
            }
        }
    }

    public DatabasePlayerPvEEventBoltaroStats getBoltaroStats() {
        return boltaroStats;
    }

    public Map<Long, DatabasePlayerPvEEventBoltaroDifficultyStats> getBoltaroEventStats() {
        return boltaroStats.getEventStats();
    }

    public DatabasePlayerPvEEventNarmerStats getNarmerStats() {
        return narmerStats;
    }

    public Map<Long, DatabasePlayerPvEEventNarmerDifficultyStats> getNarmerEventStats() {
        return narmerStats.getEventStats();
    }

    public DatabasePlayerPvEEventMithraStats getMithraStats() {
        return mithraStats;
    }

    public Map<Long, DatabasePlayerPvEEventMithraDifficultyStats> getMithraEventStats() {
        return mithraStats.getEventStats();
    }

    public DatabasePlayerPvEEventIlluminaStats getIlluminaStats() {
        return illuminaStats;
    }

    public Map<Long, DatabasePlayerPvEEventIlluminaDifficultyStats> getIlluminaEventStats() {
        return illuminaStats.getEventStats();
    }

    public DatabasePlayerPvEEventGardenOfHesperidesStats getGardenOfHesperidesStats() {
        return gardenOfHesperidesStats;
    }

    public Map<Long, DatabasePlayerPvEEventGardenOfHesperidesDifficultyStats> getGardenOfHesperidesEventStats() {
        return gardenOfHesperidesStats.getEventStats();
    }

    public DatabasePlayerPvEEventLibraryArchivesStats getLibraryArchivesStats() {
        return libraryArchivesStats;
    }

    public Map<Long, DatabasePlayerPvEEventLibraryArchivesDifficultyStats> getLibraryArchivesEventStats() {
        return libraryArchivesStats.getEventStats();
    }


    @Override
    public Collection<PvEEventStatsWarlordsClasses<DatabaseGamePvEEvent<DatabaseGamePlayerPvEEvent>, DatabaseGamePlayerPvEEvent, PvEEventStats<DatabaseGamePvEEvent<DatabaseGamePlayerPvEEvent>, DatabaseGamePlayerPvEEvent>, PvEEventStatsWarlordsSpecs<DatabaseGamePvEEvent<DatabaseGamePlayerPvEEvent>, DatabaseGamePlayerPvEEvent, PvEEventStats<DatabaseGamePvEEvent<DatabaseGamePlayerPvEEvent>, DatabaseGamePlayerPvEEvent>>>> getStats() {
        return Stream.of(boltaroStats,
                             narmerStats,
                             mithraStats,
                             illuminaStats,
                             gardenOfHesperidesStats,
                             libraryArchivesStats
                     )
                     .flatMap(s -> s.getStats()
                                    .stream()
                                    .map(ss -> (PvEEventStatsWarlordsClasses<DatabaseGamePvEEvent<DatabaseGamePlayerPvEEvent>, DatabaseGamePlayerPvEEvent, PvEEventStats<DatabaseGamePvEEvent<DatabaseGamePlayerPvEEvent>, DatabaseGamePlayerPvEEvent>, PvEEventStatsWarlordsSpecs<DatabaseGamePvEEvent<DatabaseGamePlayerPvEEvent>, DatabaseGamePlayerPvEEvent, PvEEventStats<DatabaseGamePvEEvent<DatabaseGamePlayerPvEEvent>, DatabaseGamePlayerPvEEvent>>>) (Object) ss)
                     )
                     .toList();
    }
}
