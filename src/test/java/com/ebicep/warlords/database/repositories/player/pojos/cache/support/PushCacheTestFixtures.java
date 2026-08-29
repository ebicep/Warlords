package com.ebicep.warlords.database.repositories.player.pojos.cache.support;

import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.database.repositories.events.pojos.GameEvents;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.ctf.DatabaseGameCTF;
import com.ebicep.warlords.database.repositories.games.pojos.ctf.DatabaseGamePlayerCTF;
import com.ebicep.warlords.database.repositories.games.pojos.duel.DatabaseGameDuel;
import com.ebicep.warlords.database.repositories.games.pojos.duel.DatabaseGamePlayerDuel;
import com.ebicep.warlords.database.repositories.games.pojos.interception.DatabaseGameInterception;
import com.ebicep.warlords.database.repositories.games.pojos.interception.DatabaseGamePlayerInterception;
import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePlayerPvEBase;
import com.ebicep.warlords.database.repositories.games.pojos.pve.anomaly.DatabaseGamePlayerPvEAnomaly;
import com.ebicep.warlords.database.repositories.games.pojos.pve.anomaly.DatabaseGamePvEAnomaly;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePvEEvent;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltarobonanza.DatabaseGamePlayerPvEEventBoltaroBonanza;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltarobonanza.DatabaseGamePvEEventBoltaroBonanza;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.theacropolis.DatabaseGamePlayerPvEEventTheAcropolis;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.theacropolis.DatabaseGamePvEEventTheAcropolis;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.illumina.theborderlineofillusion.DatabaseGamePlayerPvEEventTheBorderlineOfIllusion;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.illumina.theborderlineofillusion.DatabaseGamePvEEventTheBorderlineOfIllusion;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.forgottencodex.DatabaseGamePlayerPvEEventForgottenCodex;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.forgottencodex.DatabaseGamePvEEventForgottenCodex;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.mithra.spidersdwelling.DatabaseGamePlayerPvEEventSpidersDwelling;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.mithra.spidersdwelling.DatabaseGamePvEEventSpidersDwelling;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.narmer.narmerstomb.DatabaseGamePlayerPvEEventNarmersTomb;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.narmer.narmerstomb.DatabaseGamePvEEventNarmersTomb;
import com.ebicep.warlords.database.repositories.games.pojos.pve.onslaught.DatabaseGamePlayerPvEOnslaught;
import com.ebicep.warlords.database.repositories.games.pojos.pve.onslaught.DatabaseGamePvEOnslaught;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePlayerPvEWaveDefense;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePvEWaveDefense;
import com.ebicep.warlords.database.repositories.games.pojos.siege.DatabaseGamePlayerSiege;
import com.ebicep.warlords.database.repositories.games.pojos.siege.DatabaseGameSiege;
import com.ebicep.warlords.database.repositories.games.pojos.tdm.DatabaseGamePlayerTDM;
import com.ebicep.warlords.database.repositories.games.pojos.tdm.DatabaseGameTDM;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.cache.StatPushUp;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.DifficultyIndex;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public final class PushCacheTestFixtures {

    public static final UUID TEST_UUID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private PushCacheTestFixtures() {
    }

    public record GameStats(
            int kills,
            int assists,
            int deaths,
            long damage,
            long healing,
            long absorbed,
            long experienceSpec
    ) {
        public static GameStats simple(int kills, int deaths) {
            return new GameStats(kills, kills / 2, deaths, kills * 100L, kills * 20L, kills * 5L, kills * 10L);
        }
    }

    public record PvEStats(GameStats general, Map<String, Long> mobKills) {
        public static PvEStats simple(int kills, int deaths) {
            Map<String, Long> mobKills = new HashMap<>();
            mobKills.put("zombie", (long) kills);
            return new PvEStats(GameStats.simple(kills, deaths), mobKills);
        }
    }

    public record EventCase(
            String label,
            GameEvents gameEvent,
            Supplier<DatabaseGamePvEEvent<?>> gameFactory,
            Supplier<DatabaseGamePlayerPvEBase> playerFactory
    ) {
    }

    public static DatabasePlayer newWarmedPlayer() {
        DatabasePlayer databasePlayer = HeadlessDatabasePlayers.newPlayerWithPve();
        StatPushUp.warmAll(databasePlayer);
        return databasePlayer;
    }

    public static DatabasePlayer newWarmedPvePlayer() {
        DatabasePlayer databasePlayer = HeadlessDatabasePlayers.newPlayerWithPve();
        databasePlayer.getPveStats().setDatabasePlayer(databasePlayer);
        StatPushUp.warmAll(databasePlayer);
        return databasePlayer;
    }

    private static <T extends DatabaseGameBase<?>> T pubGame(T game) {
        game.setGameAddons(List.of());
        return game;
    }

    private static <T extends DatabaseGameBase<?>> T compGame(T game) {
        game.setGameAddons(List.of(GameAddon.PRIVATE_GAME));
        return game;
    }

    private static <T extends DatabaseGameBase<?>> T tournamentGame(T game) {
        game.setGameAddons(List.of(GameAddon.TOURNAMENT_MODE));
        return game;
    }

    public static DatabaseGamePlayerCTF ctfPlayer(GameStats stats) {
        DatabaseGamePlayerCTF player = DatabaseGamePlayerCTF.forTest(TEST_UUID, "tester", stats.kills(), stats.deaths());
        applyGeneralStats(player, stats);
        return player;
    }

    public static DatabaseGamePlayerTDM tdmPlayer(GameStats stats) {
        DatabaseGamePlayerTDM player = new DatabaseGamePlayerTDM();
        applyGeneralStats(player, stats);
        return player;
    }

    public static DatabaseGamePlayerInterception interceptionPlayer(GameStats stats) {
        DatabaseGamePlayerInterception player = new DatabaseGamePlayerInterception();
        applyGeneralStats(player, stats);
        return player;
    }

    public static DatabaseGamePlayerDuel duelPlayer(GameStats stats) {
        DatabaseGamePlayerDuel player = new DatabaseGamePlayerDuel();
        applyGeneralStats(player, stats);
        return player;
    }

    public static DatabaseGamePlayerSiege siegePlayer(GameStats stats) {
        DatabaseGamePlayerSiege player = new DatabaseGamePlayerSiege();
        applyGeneralStats(player, stats);
        DatabaseGamePlayerSiege specPlayer = new DatabaseGamePlayerSiege();
        applyGeneralStats(specPlayer, stats);
        TestReflection.setField(specPlayer, "spec", Specializations.PYROMANCER);
        Map<Specializations, DatabaseGamePlayerSiege> specStats = new HashMap<>();
        specStats.put(Specializations.PYROMANCER, specPlayer);
        TestReflection.setField(player, "specStats", specStats);
        return player;
    }

    public static DatabaseGamePlayerPvEOnslaught onslaughtPlayer(PvEStats stats) {
        DatabaseGamePlayerPvEOnslaught player = new DatabaseGamePlayerPvEOnslaught();
        applyPvEStats(player, stats);
        return player;
    }

    public static DatabaseGamePlayerPvEWaveDefense waveDefensePlayer(PvEStats stats) {
        DatabaseGamePlayerPvEWaveDefense player = new DatabaseGamePlayerPvEWaveDefense();
        applyPvEStats(player, stats);
        return player;
    }

    public static DatabaseGamePlayerPvEAnomaly anomalyPlayer(PvEStats stats) {
        DatabaseGamePlayerPvEAnomaly player = new DatabaseGamePlayerPvEAnomaly();
        applyPvEStats(player, stats);
        return player;
    }

    public static DatabaseGameCTF pubCtfGame() {
        return pubGame(new DatabaseGameCTF());
    }

    public static DatabaseGameTDM pubTdmGame() {
        return pubGame(new DatabaseGameTDM());
    }

    public static DatabaseGameInterception pubInterceptionGame() {
        return pubGame(new DatabaseGameInterception());
    }

    public static DatabaseGameDuel pubDuelGame() {
        return pubGame(new DatabaseGameDuel());
    }

    public static DatabaseGameSiege pubSiegeGame() {
        return pubGame(new DatabaseGameSiege());
    }

    public static DatabaseGameCTF compCtfGame() {
        return compGame(new DatabaseGameCTF());
    }

    public static DatabaseGameTDM compTdmGame() {
        return compGame(new DatabaseGameTDM());
    }

    public static DatabaseGameInterception compInterceptionGame() {
        return compGame(new DatabaseGameInterception());
    }

    public static DatabaseGameSiege compSiegeGame() {
        return compGame(new DatabaseGameSiege());
    }

    public static DatabaseGameCTF tournamentCtfGame() {
        return tournamentGame(new DatabaseGameCTF());
    }

    public static DatabaseGameTDM tournamentTdmGame() {
        return tournamentGame(new DatabaseGameTDM());
    }

    public static DatabaseGameInterception tournamentInterceptionGame() {
        return tournamentGame(new DatabaseGameInterception());
    }

    public static DatabaseGameDuel tournamentDuelGame() {
        return tournamentGame(new DatabaseGameDuel());
    }

    public static DatabaseGamePvEOnslaught pveOnslaughtGame(DatabaseGamePlayerPvEOnslaught player, int timeElapsed) {
        DatabaseGamePvEOnslaught game = new DatabaseGamePvEOnslaught();
        TestReflection.addToCollection(game, "players", player);
        configurePvEGame(game, DifficultyIndex.NORMAL, timeElapsed);
        return game;
    }

    public static DatabaseGamePvEAnomaly pveAnomalyGame(DatabaseGamePlayerPvEAnomaly player, int timeElapsed) {
        DatabaseGamePvEAnomaly game = new DatabaseGamePvEAnomaly();
        TestReflection.addToCollection(game, "players", player);
        configurePvEGame(game, DifficultyIndex.NORMAL, timeElapsed);
        return game;
    }

    public static DatabaseGamePvEWaveDefense pveWaveDefenseGame(
            DatabaseGamePlayerPvEWaveDefense player,
            DifficultyIndex difficulty,
            int wavesCleared,
            int timeElapsed
    ) {
        DatabaseGamePvEWaveDefense game = new DatabaseGamePvEWaveDefense();
        TestReflection.addToCollection(game, "players", player);
        configurePvEGame(game, difficulty, timeElapsed);
        TestReflection.setField(game, "wavesCleared", wavesCleared);
        return game;
    }

    public static DatabaseGameEvent stubCurrentGameEvent(GameEvents gameEvent) {
        Instant start = Instant.parse("2025-01-01T00:00:00Z");
        DatabaseGameEvent event = new DatabaseGameEvent(gameEvent, start, start.plus(7, ChronoUnit.DAYS));
        DatabaseGameEvent.currentGameEvent = event;
        return event;
    }

    public static void clearCurrentGameEvent() {
        DatabaseGameEvent.currentGameEvent = null;
    }

    public static List<EventCase> eventCases() {
        return List.of(
                new EventCase(
                        "boltaro",
                        GameEvents.BOLTARO,
                        DatabaseGamePvEEventBoltaroBonanza::new,
                        DatabaseGamePlayerPvEEventBoltaroBonanza::new
                ),
                new EventCase(
                        "narmer",
                        GameEvents.NARMER,
                        DatabaseGamePvEEventNarmersTomb::new,
                        DatabaseGamePlayerPvEEventNarmersTomb::new
                ),
                new EventCase(
                        "mithra",
                        GameEvents.MITHRA,
                        DatabaseGamePvEEventSpidersDwelling::new,
                        DatabaseGamePlayerPvEEventSpidersDwelling::new
                ),
                new EventCase(
                        "illumina",
                        GameEvents.ILLUMINA,
                        DatabaseGamePvEEventTheBorderlineOfIllusion::new,
                        DatabaseGamePlayerPvEEventTheBorderlineOfIllusion::new
                ),
                new EventCase(
                        "garden",
                        GameEvents.GARDEN_OF_HESPERIDES,
                        DatabaseGamePvEEventTheAcropolis::new,
                        DatabaseGamePlayerPvEEventTheAcropolis::new
                ),
                new EventCase(
                        "library",
                        GameEvents.LIBRARY_ARCHIVES,
                        DatabaseGamePvEEventForgottenCodex::new,
                        DatabaseGamePlayerPvEEventForgottenCodex::new
                )
        );
    }

    public static DatabaseGamePvEEvent<?> pveEventGame(EventCase eventCase, DatabaseGamePlayerPvEBase player, int timeElapsed) {
        DatabaseGamePvEEvent<?> game = eventCase.gameFactory().get();
        TestReflection.addToCollection(game, "players", player);
        configurePvEGame(game, DifficultyIndex.NORMAL, timeElapsed);
        return game;
    }

    private static void configurePvEGame(Object game, DifficultyIndex difficulty, int timeElapsed) {
        TestReflection.setField(game, "difficulty", difficulty);
        TestReflection.setField(game, "timeElapsed", timeElapsed);
    }

    private static void applyGeneralStats(DatabaseGamePlayerBase player, GameStats stats) {
        TestReflection.setField(player, "uuid", TEST_UUID);
        TestReflection.setField(player, "name", "tester");
        TestReflection.setField(player, "totalKills", stats.kills());
        TestReflection.setField(player, "totalAssists", stats.assists());
        TestReflection.setField(player, "totalDeaths", stats.deaths());
        TestReflection.setField(player, "totalDamage", stats.damage());
        TestReflection.setField(player, "totalHealing", stats.healing());
        TestReflection.setField(player, "totalAbsorbed", stats.absorbed());
        TestReflection.setField(player, "experienceEarnedSpec", stats.experienceSpec());
        TestReflection.setField(player, "spec", Specializations.PYROMANCER);
    }

    public static void configurePvEPlayer(DatabaseGamePlayerPvEBase player, PvEStats stats) {
        applyPvEStats(player, stats);
    }

    private static void applyPvEStats(DatabaseGamePlayerPvEBase player, PvEStats stats) {
        applyGeneralStats(player, stats.general());
        TestReflection.setField(player, "mobKills", new HashMap<>(stats.mobKills()));
        TestReflection.setField(player, "mobAssists", new HashMap<String, Long>());
        TestReflection.setField(player, "mobDeaths", new HashMap<String, Long>());
    }
}
