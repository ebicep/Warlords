package com.ebicep.warlords.database.repositories.player.pojos.cache.support;

import com.ebicep.warlords.database.repositories.player.pojos.cache.CachedPvEStats;
import com.ebicep.warlords.database.repositories.player.pojos.cache.CachedWaveDefenseStats;
import com.ebicep.warlords.database.repositories.player.pojos.cache.PushedStatTotals;
import com.ebicep.warlords.database.repositories.player.pojos.cache.PushedStatsOwner;
import com.ebicep.warlords.database.repositories.player.pojos.cache.StatPushUp;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.fail;

public final class PushCacheVerifier {

    private PushCacheVerifier() {
    }

    public static void assertConsistent(DatabasePlayer databasePlayer) {
        int index = 0;
        for (PushedStatsOwner owner : StatPushUp.selectedOwners(databasePlayer)) {
            assertRebuildStable(owner.getClass().getSimpleName() + "[" + index++ + "]", owner);
        }
    }

    private static void assertRebuildStable(String label, PushedStatsOwner owner) {
        StatsSnapshot before = StatsSnapshot.capture(owner);
        owner.rebuildPushedStats();
        StatsSnapshot after = StatsSnapshot.capture(owner);
        if (!before.equals(after)) {
            fail(label + " rebuild changed pushed totals: before=" + before + " after=" + after);
        }
    }

    private record StatsSnapshot(
            boolean warmed,
            int kills,
            int assists,
            int deaths,
            int wins,
            int losses,
            int plays,
            long damage,
            long healing,
            long absorbed,
            long experience,
            Long totalTimePlayed,
            Integer totalWavesCleared,
            Map<String, Long> mobKills,
            Map<String, Long> mobAssists,
            Map<String, Long> mobDeaths
    ) {
        static StatsSnapshot capture(PushedStatsOwner owner) {
            PushedStatTotals totals = owner.pushedStats();
            boolean pve = owner instanceof CachedPvEStats;
            return new StatsSnapshot(
                    totals.isWarmed(),
                    totals.getKills(),
                    totals.getAssists(),
                    totals.getDeaths(),
                    totals.getWins(),
                    totals.getLosses(),
                    totals.getPlays(),
                    totals.getDamage(),
                    totals.getHealing(),
                    totals.getAbsorbed(),
                    totals.getExperience(),
                    pve ? totals.getTotalTimePlayed() : null,
                    owner instanceof CachedWaveDefenseStats ? totals.getTotalWavesCleared() : null,
                    pve ? Map.copyOf(totals.getMobKillsView()) : null,
                    pve ? Map.copyOf(totals.getMobAssistsView()) : null,
                    pve ? Map.copyOf(totals.getMobDeathsView()) : null
            );
        }
    }
}
