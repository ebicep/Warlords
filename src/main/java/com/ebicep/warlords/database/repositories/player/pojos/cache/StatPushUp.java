package com.ebicep.warlords.database.repositories.player.pojos.cache;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePlayerPvEBase;
import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePvEBase;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePvEWaveDefense;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayerCompStats;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayerPubStats;
import com.ebicep.warlords.database.repositories.player.pojos.general.TournamentStats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense.DatabasePlayerWaveDefenseStats;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Applies game deltas to selected ancestor caches and warms them from leaf tree-walks.
 */
public final class StatPushUp {

    private StatPushUp() {
    }

    public static void applyPvE(
            PushedStatTotals totals,
            DatabaseGamePlayerBase gamePlayer,
            DatabaseGamePlayerResult result,
            DatabaseGamePvEBase<?> databaseGame,
            int multiplier
    ) {
        if (totals == null) {
            return;
        }
        totals.applyGeneral(gamePlayer, result, multiplier);
        if (gamePlayer instanceof DatabaseGamePlayerPvEBase pvePlayer) {
            int timeElapsed = databaseGame != null ? databaseGame.getTimeElapsed() : 0;
            totals.applyPvE(pvePlayer, timeElapsed, multiplier);
        }
        if (databaseGame instanceof DatabaseGamePvEWaveDefense waveDefenseGame) {
            totals.applyTotalWavesCleared(waveDefenseGame.getWavesCleared(), multiplier);
        }
    }

    public static void warmAll(DatabasePlayer databasePlayer) {
        for (PushedStatsOwner owner : selectedOwners(databasePlayer)) {
            owner.warmPushedStats();
        }
    }

    public static void rebuildSelectedCaches(DatabasePlayer databasePlayer) {
        invalidateAll(databasePlayer);
        warmAll(databasePlayer);
    }

    public static void invalidateAll(DatabasePlayer databasePlayer) {
        for (PushedStatsOwner owner : selectedOwners(databasePlayer)) {
            owner.invalidatePushedStats();
        }
    }

    /**
     * Selected push-up ancestors. Warm, invalidate, and verification must all use this canonical traversal.
     */
    public static List<PushedStatsOwner> selectedOwners(DatabasePlayer databasePlayer) {
        List<PushedStatsOwner> owners = new ArrayList<>();
        if (databasePlayer == null) {
            return List.of();
        }
        owners.add(databasePlayer);
        DatabasePlayerPubStats pubStats = databasePlayer.getPubStats();
        if (pubStats != null) {
            owners.add(pubStats);
            owners.add(pubStats.getCtfStats());
            owners.add(pubStats.getTdmStats());
            owners.add(pubStats.getInterceptionStats());
            owners.add(pubStats.getDuelStats());
            owners.add(pubStats.getSiegeStats());
        }
        DatabasePlayerCompStats compStats = databasePlayer.getCompStats();
        if (compStats != null) {
            owners.add(compStats);
            owners.add(compStats.getCtfStats());
            owners.add(compStats.getTdmStats());
            owners.add(compStats.getInterceptionStats());
            if (compStats.getSiegeStats() != null) {
                owners.add(compStats.getSiegeStats());
            }
        }
        TournamentStats tournamentStats = databasePlayer.getTournamentStats();
        if (tournamentStats != null) {
            owners.add(tournamentStats);
            for (TournamentStats.DatabasePlayerTournamentStats snapshot : tournamentStats.getAllTournamentStats()) {
                owners.add(snapshot);
                owners.add(snapshot.getCtfStats());
                owners.add(snapshot.getTdmStats());
                owners.add(snapshot.getInterceptionStats());
                owners.add(snapshot.getDuelStats());
            }
        }
        DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
        if (pveStats != null) {
            owners.add(pveStats);
            DatabasePlayerWaveDefenseStats waveDefenseStats = pveStats.getWaveDefenseStats();
            owners.add(waveDefenseStats);
            owners.add(waveDefenseStats.getEasyStats());
            owners.add(waveDefenseStats.getNormalStats());
            owners.add(waveDefenseStats.getHardStats());
            owners.add(waveDefenseStats.getExtremeStats());
            owners.add(waveDefenseStats.getEndlessStats());
            owners.add(pveStats.getOnslaughtStats());
            owners.add(pveStats.getAnomalyStats());
            owners.add(pveStats.getEventStats());
        }
        owners.removeIf(Objects::isNull);
        return List.copyOf(owners);
    }

    public static boolean mapsEqual(Map<String, Long> pushed, Map<String, Long> treeWalk) {
        return Objects.equals(normalize(pushed), normalize(treeWalk));
    }

    private static Map<String, Long> normalize(Map<String, Long> map) {
        Map<String, Long> normalized = new java.util.TreeMap<>();
        if (map == null) {
            return normalized;
        }
        map.forEach((key, value) -> {
            if (value != null && value != 0) {
                normalized.put(key, value);
            }
        });
        return normalized;
    }
}
