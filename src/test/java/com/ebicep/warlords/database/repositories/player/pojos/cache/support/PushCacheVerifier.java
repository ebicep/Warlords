package com.ebicep.warlords.database.repositories.player.pojos.cache.support;

import com.ebicep.warlords.database.repositories.player.pojos.cache.CachedPvEStats;
import com.ebicep.warlords.database.repositories.player.pojos.cache.CachedWaveDefenseStats;
import com.ebicep.warlords.database.repositories.player.pojos.cache.PushedMultiPvEStats;
import com.ebicep.warlords.database.repositories.player.pojos.cache.PushedStatTotals;
import com.ebicep.warlords.database.repositories.player.pojos.cache.PushedStatsOwner;
import com.ebicep.warlords.database.repositories.player.pojos.cache.PushedStatsWarlordsClasses;
import com.ebicep.warlords.database.repositories.player.pojos.cache.StatPushUp;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.player.general.Classes;

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
            Long totalMobKills,
            Map<String, Long> mobKills,
            Map<String, Long> mobAssists,
            Map<String, Long> mobDeaths,
            Integer highestWaveCleared,
            Long fastestGameFinished,
            Long mostDamageInWave,
            long longestTicksLived,
            Integer flagsCaptured,
            Integer flagsReturned,
            Map<Classes, Long> classExperience
    ) {
        static StatsSnapshot capture(PushedStatsOwner owner) {
            PushedStatTotals totals = owner.pushedStats();
            boolean pve = owner instanceof CachedPvEStats;
            boolean waveDefense = owner instanceof CachedWaveDefenseStats;
            boolean onslaught = owner instanceof PushedMultiPvEStats.Onslaught;
            boolean ctf = owner instanceof PushedStatsWarlordsClasses.CTF;
            Map<Classes, Long> classExperience = null;
            if (pve || waveDefense || onslaught) {
                classExperience = new java.util.EnumMap<>(Classes.class);
                for (Classes classes : Classes.VALUES) {
                    long value = totals.getClassExperience(classes);
                    if (value != 0) {
                        classExperience.put(classes, value);
                    }
                }
            }
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
                    waveDefense ? totals.getTotalWavesCleared() : null,
                    pve ? totals.getTotalMobKills() : null,
                    pve ? Map.copyOf(totals.getMobKillsView()) : null,
                    pve ? Map.copyOf(totals.getMobAssistsView()) : null,
                    pve ? Map.copyOf(totals.getMobDeathsView()) : null,
                    waveDefense ? totals.getHighestWaveCleared() : null,
                    waveDefense ? totals.getFastestGameFinished() : null,
                    waveDefense ? totals.getMostDamageInWave() : null,
                    onslaught ? totals.getLongestTicksLived() : 0,
                    ctf ? totals.getFlagsCaptured() : null,
                    ctf ? totals.getFlagsReturned() : null,
                    classExperience
            );
        }
    }
}
