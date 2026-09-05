package com.ebicep.warlords.database.repositories.player.pojos.cache;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePlayerPvEBase;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.Specializations;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Transient aggregate totals for a selected push-up level. Never serialized.
 */
public final class PushedStatTotals {

    private boolean warmed;

    private int kills;
    private int assists;
    private int deaths;
    private int wins;
    private int losses;
    private int plays;
    private long damage;
    private long healing;
    private long absorbed;
    private long experience;
    private long totalTimePlayed;
    private int totalWavesCleared;
    private long totalMobKills;
    private int highestWaveCleared;
    private long fastestGameFinished;
    private long mostDamageInWave;
    private long longestTicksLived;
    private int flagsCaptured;
    private int flagsReturned;
    private final long[] classExperience = new long[Classes.VALUES.length];

    private final Map<String, Long> mobKills = new HashMap<>();
    private final Map<String, Long> mobAssists = new HashMap<>();
    private final Map<String, Long> mobDeaths = new HashMap<>();

    public boolean isWarmed() {
        return warmed;
    }

    public void markWarmed() {
        this.warmed = true;
    }

    public void invalidate() {
        this.warmed = false;
        this.kills = 0;
        this.assists = 0;
        this.deaths = 0;
        this.wins = 0;
        this.losses = 0;
        this.plays = 0;
        this.damage = 0;
        this.healing = 0;
        this.absorbed = 0;
        this.experience = 0;
        this.totalTimePlayed = 0;
        this.totalWavesCleared = 0;
        this.totalMobKills = 0;
        this.highestWaveCleared = 0;
        this.fastestGameFinished = 0;
        this.mostDamageInWave = 0;
        this.longestTicksLived = 0;
        this.flagsCaptured = 0;
        this.flagsReturned = 0;
        Arrays.fill(this.classExperience, 0L);
        this.mobKills.clear();
        this.mobAssists.clear();
        this.mobDeaths.clear();
    }

    public void fillGeneral(
            int kills,
            int assists,
            int deaths,
            int wins,
            int losses,
            int plays,
            long damage,
            long healing,
            long absorbed,
            long experience
    ) {
        this.kills = kills;
        this.assists = assists;
        this.deaths = deaths;
        this.wins = wins;
        this.losses = losses;
        this.plays = plays;
        this.damage = damage;
        this.healing = healing;
        this.absorbed = absorbed;
        this.experience = experience;
    }

    public void fillPvE(long totalTimePlayed, Map<String, Long> mobKills, Map<String, Long> mobAssists, Map<String, Long> mobDeaths) {
        this.totalTimePlayed = totalTimePlayed;
        replaceMap(this.mobKills, mobKills);
        replaceMap(this.mobAssists, mobAssists);
        replaceMap(this.mobDeaths, mobDeaths);
        this.totalMobKills = sumValues(this.mobKills);
    }

    public void fillTotalWavesCleared(int totalWavesCleared) {
        this.totalWavesCleared = totalWavesCleared;
    }

    public void fillWaveDefense(int highestWaveCleared, long fastestGameFinished, long mostDamageInWave) {
        this.highestWaveCleared = highestWaveCleared;
        this.fastestGameFinished = fastestGameFinished;
        this.mostDamageInWave = mostDamageInWave;
    }

    public void fillLongestTicksLived(long longestTicksLived) {
        this.longestTicksLived = longestTicksLived;
    }

    public void fillFlags(int flagsCaptured, int flagsReturned) {
        this.flagsCaptured = flagsCaptured;
        this.flagsReturned = flagsReturned;
    }

    public void fillClassExperience(long[] classExperience) {
        Arrays.fill(this.classExperience, 0L);
        if (classExperience == null) {
            return;
        }
        int len = Math.min(this.classExperience.length, classExperience.length);
        System.arraycopy(classExperience, 0, this.classExperience, 0, len);
    }

    /**
     * Warms from a tree-walk snapshot via {@code fill*}. No-op if already warmed.
     * Caller must pass Multi*.super / StatsWarlordsClasses.super values, not pushed getters.
     */
    public void warm(Runnable fill) {
        if (warmed) {
            return;
        }
        fill.run();
        markWarmed();
    }

    public void applyGeneral(DatabaseGamePlayerBase gamePlayer, DatabaseGamePlayerResult result, int multiplier) {
        // Intentional no-op while cold; next tree-walk warm (StatPushUp.warmAll on load) is source of truth.
        if (!warmed || gamePlayer == null) {
            return;
        }
        this.kills += gamePlayer.getTotalKills() * multiplier;
        this.assists += gamePlayer.getTotalAssists() * multiplier;
        this.deaths += gamePlayer.getTotalDeaths() * multiplier;
        if (result != null) {
            switch (result) {
                case WON -> this.wins += multiplier;
                case LOST, DRAW -> this.losses += multiplier;
                case NONE -> {
                }
            }
        }
        this.plays += multiplier;
        this.damage += gamePlayer.getTotalDamage() * multiplier;
        this.healing += gamePlayer.getTotalHealing() * multiplier;
        this.absorbed += gamePlayer.getTotalAbsorbed() * multiplier;
        long experienceDelta = gamePlayer.getExperienceEarnedSpec() * multiplier;
        this.experience += experienceDelta;
        Specializations spec = gamePlayer.getSpec();
        if (spec != null && experienceDelta != 0) {
            Classes classes = Specializations.getClass(spec);
            if (classes != null) {
                this.classExperience[classes.ordinal()] += experienceDelta;
            }
        }
    }

    public void applyFlags(int flagsCaptured, int flagsReturned, int multiplier) {
        if (!warmed) {
            return;
        }
        this.flagsCaptured += flagsCaptured * multiplier;
        this.flagsReturned += flagsReturned * multiplier;
    }

    public void applyClassExperience(Classes classes, long delta, int multiplier) {
        if (!warmed || classes == null || delta == 0) {
            return;
        }
        this.classExperience[classes.ordinal()] += delta * multiplier;
    }

    public void applyPvE(DatabaseGamePlayerPvEBase gamePlayer, int timeElapsed, int multiplier) {
        // Intentional no-op while cold; next tree-walk warm (StatPushUp.warmAll on load) is source of truth.
        if (!warmed || gamePlayer == null) {
            return;
        }
        this.totalTimePlayed += (long) timeElapsed * multiplier;
        mergeMobKills(gamePlayer.getMobKills(), multiplier);
        mergeMobMap(this.mobAssists, gamePlayer.getMobAssists(), multiplier);
        mergeMobMap(this.mobDeaths, gamePlayer.getMobDeaths(), multiplier);
    }

    public void applyTotalWavesCleared(int wavesCleared, int multiplier) {
        if (!warmed) {
            return;
        }
        this.totalWavesCleared += wavesCleared * multiplier;
    }

    public void applyWaveDefense(int wavesCleared, int maxWaves, int timeElapsed, long mostDamageInWave, int multiplier) {
        if (!warmed) {
            return;
        }
        if (multiplier > 0) {
            this.highestWaveCleared = Math.max(wavesCleared, this.highestWaveCleared);
        } else if (this.highestWaveCleared == wavesCleared) {
            this.highestWaveCleared = 0;
        }
        if (maxWaves > 0 && wavesCleared == maxWaves) {
            if (multiplier > 0) {
                if (this.fastestGameFinished == 0 || timeElapsed < this.fastestGameFinished) {
                    this.fastestGameFinished = timeElapsed;
                }
            } else if (this.fastestGameFinished == timeElapsed) {
                this.fastestGameFinished = 0;
            }
        }
        if (multiplier > 0) {
            this.mostDamageInWave = Math.max(this.mostDamageInWave, mostDamageInWave);
        } else if (this.mostDamageInWave == mostDamageInWave) {
            this.mostDamageInWave = 0;
        }
    }

    public void applyLongestTicksLived(int timeElapsed, int multiplier) {
        if (!warmed) {
            return;
        }
        if (multiplier > 0) {
            this.longestTicksLived = Math.max((long) timeElapsed * multiplier, this.longestTicksLived);
        } else if (this.longestTicksLived == timeElapsed) {
            this.longestTicksLived = 0;
        }
    }

    public int getKills() {
        return kills;
    }

    public int getAssists() {
        return assists;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public int getPlays() {
        return plays;
    }

    public long getDamage() {
        return damage;
    }

    public long getHealing() {
        return healing;
    }

    public long getAbsorbed() {
        return absorbed;
    }

    public long getExperience() {
        return experience;
    }

    public long getTotalTimePlayed() {
        return totalTimePlayed;
    }

    public int getTotalWavesCleared() {
        return totalWavesCleared;
    }

    public long getTotalMobKills() {
        return totalMobKills;
    }

    public int getHighestWaveCleared() {
        return highestWaveCleared;
    }

    public long getFastestGameFinished() {
        return fastestGameFinished;
    }

    public long getMostDamageInWave() {
        return mostDamageInWave;
    }

    public long getLongestTicksLived() {
        return longestTicksLived;
    }

    public int getFlagsCaptured() {
        return flagsCaptured;
    }

    public int getFlagsReturned() {
        return flagsReturned;
    }

    public long getClassExperience(Classes classes) {
        if (classes == null) {
            return 0;
        }
        return classExperience[classes.ordinal()];
    }

    public Map<String, Long> getMobKillsView() {
        return Collections.unmodifiableMap(mobKills);
    }

    public Map<String, Long> getMobAssistsView() {
        return Collections.unmodifiableMap(mobAssists);
    }

    public Map<String, Long> getMobDeathsView() {
        return Collections.unmodifiableMap(mobDeaths);
    }

    public long getMobKillCount(String mobName) {
        if (mobName == null || mobName.isEmpty()) {
            return 0;
        }
        Long value = mobKills.get(mobName);
        return value == null ? 0 : value;
    }

    private void mergeMobKills(Map<String, Long> delta, int multiplier) {
        if (delta == null || delta.isEmpty()) {
            return;
        }
        delta.forEach((key, count) -> {
            if (count == null || count == 0) {
                return;
            }
            long add = count * (long) multiplier;
            this.totalMobKills += add;
            long next = this.mobKills.merge(key, add, Long::sum);
            if (next == 0) {
                this.mobKills.remove(key);
            }
        });
    }

    private static void replaceMap(Map<String, Long> target, Map<String, Long> source) {
        target.clear();
        if (source != null) {
            source.forEach((key, value) -> {
                if (value != null && value != 0) {
                    target.put(key, value);
                }
            });
        }
    }

    private static void mergeMobMap(Map<String, Long> cache, Map<String, Long> delta, int multiplier) {
        if (delta == null || delta.isEmpty()) {
            return;
        }
        delta.forEach((key, count) -> {
            if (count == null || count == 0) {
                return;
            }
            long next = cache.merge(key, count * (long) multiplier, Long::sum);
            if (next == 0) {
                cache.remove(key);
            }
        });
    }

    private static long sumValues(Map<String, Long> map) {
        long sum = 0;
        for (Long value : map.values()) {
            if (value != null) {
                sum += value;
            }
        }
        return sum;
    }
}
