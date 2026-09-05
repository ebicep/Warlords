package com.ebicep.warlords.database.repositories.player.pojos.pve.events;

import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.database.repositories.events.pojos.GameEvents;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePlayerPvEEvent;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePvEEvent;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.DatabaseGamePlayerPvEEventBoltaro;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.DatabaseGamePvEEventBoltaro;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltarobonanza.DatabaseGamePlayerPvEEventBoltaroBonanza;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltarobonanza.DatabaseGamePvEEventBoltaroBonanza;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltaroslair.DatabaseGamePlayerPvEEventBoltarosLair;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltaroslair.DatabaseGamePvEEventBoltaroLair;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.DatabaseGamePlayerPvEEventGardenOfHesperides;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.DatabaseGamePvEEventGardenOfHesperides;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.tartarus.DatabaseGamePlayerPvEEventTartarus;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.tartarus.DatabaseGamePvEEventTartarus;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.theacropolis.DatabaseGamePlayerPvEEventTheAcropolis;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.theacropolis.DatabaseGamePvEEventTheAcropolis;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.illumina.DatabaseGamePlayerPvEEventIllumina;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.illumina.DatabaseGamePvEEventIllumina;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.illumina.theborderlineofillusion.DatabaseGamePlayerPvEEventTheBorderlineOfIllusion;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.illumina.theborderlineofillusion.DatabaseGamePvEEventTheBorderlineOfIllusion;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.DatabaseGamePlayerPvEEventLibraryArchives;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.DatabaseGamePvEEventLibraryArchives;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.forgottencodex.DatabaseGamePlayerPvEEventForgottenCodex;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.forgottencodex.DatabaseGamePvEEventForgottenCodex;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.grimoiresgraveyard.DatabaseGamePlayerPvEEventGrimoiresGraveyard;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.grimoiresgraveyard.DatabaseGamePvEEventGrimoiresGraveyard;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.mithra.DatabaseGamePlayerPvEEventMithra;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.mithra.DatabaseGamePvEEventMithra;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.mithra.spidersdwelling.DatabaseGamePlayerPvEEventSpidersDwelling;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.mithra.spidersdwelling.DatabaseGamePvEEventSpidersDwelling;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.narmer.DatabaseGamePlayerPvEEventNarmer;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.narmer.DatabaseGamePvEEventNarmer;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.narmer.narmerstomb.DatabaseGamePlayerPvEEventNarmersTomb;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.narmer.narmerstomb.DatabaseGamePvEEventNarmersTomb;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.TracksAbilityStats;
import com.ebicep.warlords.database.repositories.player.pojos.TracksMultiAbilityStats;
import com.ebicep.warlords.database.repositories.player.pojos.cache.PushedMultiPvEStats;
import com.ebicep.warlords.database.repositories.player.pojos.cache.PushedStatTotals;
import com.ebicep.warlords.database.repositories.player.pojos.cache.StatPushUp;
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
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.Pair;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Collection;
import java.util.List;
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
                        DatabaseGamePlayerPvEEvent>>>,
        TracksMultiAbilityStats,
        PushedMultiPvEStats {

    @Transient
    private final PushedStatTotals pushedStats = new PushedStatTotals();

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
        updateModeStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
    }

    /**
     * @return true if an event leaf was updated and local push-up was applied
     */
    public boolean updateModeStats(
            DatabasePlayer databasePlayer,
            DatabaseGamePvEEvent databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEEvent gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        DatabaseGameEvent currentGameEvent = DatabaseGameEvent.currentGameEvent;
        if (currentGameEvent == null) {
            return false;
        }
        if (databaseGame.getBasePlayers().isEmpty() || !isSupportedEventPair(databaseGame, gamePlayer)) {
            return false;
        }
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
            return false;
        }
        StatPushUp.applyPvE(pushedStats, gamePlayer, result, databaseGame, multiplier);
        //GUILDS
        if (playersCollection == PlayersCollections.LIFETIME) {
            Pair<Guild, GuildPlayer> guildGuildPlayerPair = GuildManager.getGuildAndGuildPlayerFromPlayer(gamePlayer.getUuid());
            if (guildGuildPlayerPair != null) {
                Guild guild = guildGuildPlayerPair.getA();
                GuildPlayer guildPlayer = guildGuildPlayerPair.getB();

                long points = Math.min(gamePlayer.getPoints(), databaseGame.getPointLimit()) * multiplier;
                guild.addEventPoints(event, currentGameEvent.getStartDateSecond(), points * multiplier);
                guildPlayer.addEventPoints(event, currentGameEvent.getStartDateSecond(), points * multiplier);
                guild.queueUpdate();
            }
        }
        return true;
    }

    private static boolean isSupportedEventPair(DatabaseGamePvEEvent databaseGame, DatabaseGamePlayerPvEEvent gamePlayer) {
        return databaseGame instanceof DatabaseGamePvEEventBoltaroBonanza
                && gamePlayer instanceof DatabaseGamePlayerPvEEventBoltaroBonanza
                || databaseGame instanceof DatabaseGamePvEEventBoltaroLair
                && gamePlayer instanceof DatabaseGamePlayerPvEEventBoltarosLair
                || databaseGame instanceof DatabaseGamePvEEventNarmersTomb
                && gamePlayer instanceof DatabaseGamePlayerPvEEventNarmersTomb
                || databaseGame instanceof DatabaseGamePvEEventSpidersDwelling
                && gamePlayer instanceof DatabaseGamePlayerPvEEventSpidersDwelling
                || databaseGame instanceof DatabaseGamePvEEventTheBorderlineOfIllusion
                && gamePlayer instanceof DatabaseGamePlayerPvEEventTheBorderlineOfIllusion
                || databaseGame instanceof DatabaseGamePvEEventTheAcropolis
                && gamePlayer instanceof DatabaseGamePlayerPvEEventTheAcropolis
                || databaseGame instanceof DatabaseGamePvEEventTartarus
                && gamePlayer instanceof DatabaseGamePlayerPvEEventTartarus
                || databaseGame instanceof DatabaseGamePvEEventForgottenCodex
                && gamePlayer instanceof DatabaseGamePlayerPvEEventForgottenCodex
                || databaseGame instanceof DatabaseGamePvEEventGrimoiresGraveyard
                && gamePlayer instanceof DatabaseGamePlayerPvEEventGrimoiresGraveyard;
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

    @Override
    public Collection<TracksAbilityStats> getAllAbilityStats() {
        return List.of(boltaroStats, narmerStats, mithraStats, illuminaStats, gardenOfHesperidesStats, libraryArchivesStats);
    }

    @Override
    public PushedStatTotals pushedStats() {
        return pushedStats;
    }

    @Override
    public void warmPushedStats() {
        pushedStats.warm(() -> {
            pushedStats.fillGeneral(
                    MultiPvEEventStats.super.getKills(),
                    MultiPvEEventStats.super.getAssists(),
                    MultiPvEEventStats.super.getDeaths(),
                    MultiPvEEventStats.super.getWins(),
                    MultiPvEEventStats.super.getLosses(),
                    MultiPvEEventStats.super.getPlays(),
                    MultiPvEEventStats.super.getDamage(),
                    MultiPvEEventStats.super.getHealing(),
                    MultiPvEEventStats.super.getAbsorbed(),
                    MultiPvEEventStats.super.getExperience()
            );
            pushedStats.fillPvE(
                    MultiPvEEventStats.super.getTotalTimePlayed(),
                    MultiPvEEventStats.super.getMobKills(),
                    MultiPvEEventStats.super.getMobAssists(),
                    MultiPvEEventStats.super.getMobDeaths()
            );
            long[] classXp = new long[Classes.VALUES.length];
            for (Classes classes : Classes.VALUES) {
                classXp[classes.ordinal()] = MultiPvEEventStats.super.getStat(classes, Stats::getExperience, Long::sum, 0L);
            }
            pushedStats.fillClassExperience(classXp);
        });
    }

    @Override
    public int getKills() {
        return PushedMultiPvEStats.super.getKills();
    }

    @Override
    public int getAssists() {
        return PushedMultiPvEStats.super.getAssists();
    }

    @Override
    public int getDeaths() {
        return PushedMultiPvEStats.super.getDeaths();
    }

    @Override
    public int getWins() {
        return PushedMultiPvEStats.super.getWins();
    }

    @Override
    public int getLosses() {
        return PushedMultiPvEStats.super.getLosses();
    }

    @Override
    public int getPlays() {
        return PushedMultiPvEStats.super.getPlays();
    }

    @Override
    public long getDamage() {
        return PushedMultiPvEStats.super.getDamage();
    }

    @Override
    public long getHealing() {
        return PushedMultiPvEStats.super.getHealing();
    }

    @Override
    public long getAbsorbed() {
        return PushedMultiPvEStats.super.getAbsorbed();
    }

    @Override
    public long getExperience() {
        return PushedMultiPvEStats.super.getExperience();
    }

    @Override
    public long getTotalTimePlayed() {
        return PushedMultiPvEStats.super.getTotalTimePlayed();
    }

    @Override
    public Map<String, Long> getMobKills() {
        return PushedMultiPvEStats.super.getMobKills();
    }

    @Override
    public Map<String, Long> getMobAssists() {
        return PushedMultiPvEStats.super.getMobAssists();
    }

    @Override
    public Map<String, Long> getMobDeaths() {
        return PushedMultiPvEStats.super.getMobDeaths();
    }

    @Override
    public long getTotalMobKills() {
        return PushedMultiPvEStats.super.getTotalMobKills();
    }

    public int treeWalkKills() {
        return MultiPvEEventStats.super.getKills();
    }

    public Map<String, Long> treeWalkMobKills() {
        return MultiPvEEventStats.super.getMobKills();
    }
}
