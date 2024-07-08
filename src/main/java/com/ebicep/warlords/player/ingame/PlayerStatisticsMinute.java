package com.ebicep.warlords.player.ingame;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Stores player statistics grouped by every minute
 */
public class PlayerStatisticsMinute implements Iterable<PlayerStatisticsMinute.Entry> {

    /**
     * A list of every entry we have
     */
    @Nonnull
    private final List<Entry> entries;
    /**
     * Entry where current statics will be inserted into
     */
    @Nonnull
    private Entry current = new Entry();
    /**
     * Cache variable to prevent repeated total computations.
     */
    @CheckForNull
    private transient Entry total = new Entry();

    public PlayerStatisticsMinute() {
        this(16);
    }

    public PlayerStatisticsMinute(int expectedGameDurationInMinutes) {
        entries = new ArrayList<>(expectedGameDurationInMinutes);
        entries.add(current);
    }

    public void advanceMinute() {
        current = new Entry();
        entries.add(current);
    }

    public void addKill() {
        current.kills++;
        if (this.total != null) {
            this.total.kills++;
        }
    }

    public void addAssist() {
        current.assists++;
        if (this.total != null) {
            this.total.assists++;
        }
    }

    public void addDeath() {
        current.deaths++;
        if (this.total != null) {
            this.total.deaths++;
        }
    }

    public void addDamage(long damage) {
        current.damage += damage;
        if (this.total != null) {
            this.total.damage += damage;
        }
    }

    public void addHealing(long healing) {
        current.healing += healing;
        if (this.total != null) {
            this.total.healing += healing;
        }
    }

    public void addAbsorbed(long absorbed) {
        current.absorbed += absorbed;
        if (this.total != null) {
            this.total.absorbed += absorbed;
        }
    }

    public void addDamageOnCarrier(long damageOnCarrier) {
        current.damageOnCarrier += damageOnCarrier;
        if (this.total != null) {
            this.total.damageOnCarrier += damageOnCarrier;
        }
    }

    public void addHealingOnCarrier(long healingOnCarrier) {
        current.healingOnCarrier += healingOnCarrier;
        if (this.total != null) {
            this.total.healingOnCarrier += healingOnCarrier;
        }
    }

    public void addFlagCapture() {
        current.flagsCaptured++;
        if (this.total != null) {
            this.total.flagsCaptured++;
        }
    }

    public void addFlagReturned() {
        current.flagsReturned++;
        if (this.total != null) {
            this.total.flagsReturned++;
        }
    }

    public void addTimeInCombat() {
        current.timeInCombat++;
        if (this.total != null) {
            this.total.timeInCombat++;
        }
    }

    public void addTotalRespawnTime() {
        current.respawnTimeSpent++;
        if (this.total != null) {
            this.total.respawnTimeSpent++;
        }
    }

    public void addDamageTaken(long damageTaken) {
        current.damageTaken += damageTaken;
        if (this.total != null) {
            this.total.damageTaken += damageTaken;
        }
    }

    public void addMeleeHits() {
        current.meleeHits++;
        if (this.total != null) {
            this.total.meleeHits++;
        }
    }

    public void addJumps() {
        current.jumps++;
        if (this.total != null) {
            this.total.jumps++;
        }
    }

    public void addMobKill(String mob) {
        current.mobKills.putIfAbsent(mob, 0L);
        current.mobKills.put(mob, current.mobKills.get(mob) + 1);
        if (this.total != null) {
            this.total.mobKills.putIfAbsent(mob, 0L);
            this.total.mobKills.put(mob, this.total.mobKills.get(mob) + 1);
        }
    }

    public void addMobAssist(String mob) {
        current.mobAssists.putIfAbsent(mob, 0L);
        current.mobAssists.put(mob, current.mobAssists.get(mob) + 1);
        if (this.total != null) {
            this.total.mobAssists.putIfAbsent(mob, 0L);
            this.total.mobAssists.put(mob, this.total.mobAssists.get(mob) + 1);
        }
    }

    public void addMobDeath(String mob) {
        current.mobDeaths.putIfAbsent(mob, 0L);
        current.mobDeaths.put(mob, current.mobDeaths.get(mob) + 1);
        if (this.total != null) {
            this.total.mobDeaths.putIfAbsent(mob, 0L);
            this.total.mobDeaths.put(mob, this.total.mobDeaths.get(mob) + 1);
        }
    }

    public Entry recomputeTotal() {
        return entries.stream().reduce(new Entry(), Entry::merge);
    }

    @Nonnull
    public Entry total() {
        if (total == null) {
            total = recomputeTotal();
        }
        return total;
    }

    @Nonnull
    public List<Entry> getEntries() {
        return entries;
    }

    @Nonnull
    @Override
    public Iterator<Entry> iterator() {
        return entries.iterator();
    }

    @Override
    public void forEach(Consumer<? super Entry> action) {
        entries.forEach(action);
    }

    @Override
    public Spliterator<Entry> spliterator() {
        return entries.spliterator();
    }

    public Stream<Entry> stream() {
        return entries.stream();
    }

    public static class Entry {

        @Nonnegative
        private int kills;
        @Nonnegative
        private int assists;
        @Nonnegative
        private int deaths;

        @Nonnegative
        private long damage;
        @Nonnegative
        private long healing;
        @Nonnegative
        private long absorbed;

        @Nonnegative
        private long damageOnCarrier;
        @Nonnegative
        private long healingOnCarrier;

        @Nonnegative
        private int flagsCaptured;
        @Nonnegative
        private int flagsReturned;
        @Nonnegative
        private int timeInCombat;
        @Nonnegative
        private int respawnTimeSpent;
        @Nonnegative
        private long damageTaken;

        @Nonnegative
        private int meleeHits;
        @Nonnegative
        private int jumps;

        private Map<String, Long> mobKills = new LinkedHashMap<>();
        private Map<String, Long> mobAssists = new LinkedHashMap<>();
        private Map<String, Long> mobDeaths = new LinkedHashMap<>();

        public Entry merge(Entry other) {
            kills += other.kills;
            assists += other.assists;
            deaths += other.deaths;
            damage += other.damage;
            healing += other.healing;
            absorbed += other.absorbed;
            damageOnCarrier += other.damageOnCarrier;
            healingOnCarrier += other.healingOnCarrier;
            flagsCaptured += other.flagsCaptured;
            flagsReturned += other.flagsReturned;
            timeInCombat += other.timeInCombat;
            respawnTimeSpent += other.respawnTimeSpent;
            damageTaken += other.damageTaken;
            meleeHits += other.meleeHits;
            jumps += other.jumps;
            other.mobKills.forEach((s, aLong) -> mobKills.merge(s, aLong, Long::sum));
            other.mobAssists.forEach((s, aLong) -> mobAssists.merge(s, aLong, Long::sum));
            other.mobDeaths.forEach((s, aLong) -> mobDeaths.merge(s, aLong, Long::sum));
            return this;
        }

        @Nonnegative
        public int getKills() {
            return kills;
        }

        @Nonnegative
        public int getAssists() {
            return assists;
        }

        @Nonnegative
        public int getDeaths() {
            return deaths;
        }

        @Nonnegative
        public long getDamage() {
            return damage;
        }

        @Nonnegative
        public long getHealing() {
            return healing;
        }

        @Nonnegative
        public long getAbsorbed() {
            return absorbed;
        }

        @Nonnegative
        public long getDamageOnCarrier() {
            return damageOnCarrier;
        }

        @Nonnegative
        public long getHealingOnCarrier() {
            return healingOnCarrier;
        }

        @Nonnegative
        public int getFlagsCaptured() {
            return flagsCaptured;
        }

        @Nonnegative
        public int getFlagsReturned() {
            return flagsReturned;
        }

        @Nonnegative
        public int getTimeInCombat() {
            return timeInCombat;
        }

        @Nonnegative
        public int getRespawnTimeSpent() {
            return respawnTimeSpent;
        }

        @Nonnegative
        public long getDamageTaken() {
            return damageTaken;
        }

        public int getMeleeHits() {
            return meleeHits;
        }

        public int getJumps() {
            return jumps;
        }

        public Map<String, Long> getMobKills() {
            return mobKills;
        }

        public Map<String, Long> getMobAssists() {
            return mobAssists;
        }

        public Map<String, Long> getMobDeaths() {
            return mobDeaths;
        }

        @Override
        public String toString() {
            return "Entry{" +
                    "kills=" + kills +
                    ", assists=" + assists +
                    ", deaths=" + deaths +
                    ", damage=" + damage +
                    ", healing=" + healing +
                    ", absorbed=" + absorbed +
                    ", damageOnCarrier=" + damageOnCarrier +
                    ", healingOnCarrier=" + healingOnCarrier +
                    ", flagsCaptured=" + flagsCaptured +
                    ", flagsReturned=" + flagsReturned +
                    ", timeInCombat=" + timeInCombat +
                    ", respawnTimeSpent=" + respawnTimeSpent +
                    ", damageTaken=" + damageTaken +
                    ", mobKills=" + mobKills +
                    ", mobAssists=" + mobAssists +
                    ", mobDeaths=" + mobDeaths +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Entry entry = (Entry) o;
            return kills == entry.kills &&
                    assists == entry.assists &&
                    deaths == entry.deaths &&
                    damage == entry.damage &&
                    healing == entry.healing &&
                    absorbed == entry.absorbed &&
                    damageOnCarrier == entry.damageOnCarrier &&
                    healingOnCarrier == entry.healingOnCarrier &&
                    flagsCaptured == entry.flagsCaptured &&
                    flagsReturned == entry.flagsReturned &&
                    timeInCombat == entry.timeInCombat &&
                    respawnTimeSpent == entry.respawnTimeSpent &&
                    damageTaken == entry.damageTaken &&
                    meleeHits == entry.meleeHits &&
                    jumps == entry.jumps &&
                    mobKills.equals(entry.mobKills) &&
                    mobAssists.equals(entry.mobAssists) &&
                    mobDeaths.equals(entry.mobDeaths);
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 13 * hash + this.kills;
            hash = 13 * hash + this.assists;
            hash = 13 * hash + this.deaths;
            hash = 13 * hash + (int) (this.damage ^ (this.damage >>> 32));
            hash = 13 * hash + (int) (this.healing ^ (this.healing >>> 32));
            hash = 13 * hash + (int) (this.absorbed ^ (this.absorbed >>> 32));
            hash = 13 * hash + (int) (this.damageOnCarrier ^ (this.damageOnCarrier >>> 32));
            hash = 13 * hash + (int) (this.healingOnCarrier ^ (this.healingOnCarrier >>> 32));
            hash = 13 * hash + this.flagsCaptured;
            hash = 13 * hash + this.flagsReturned;
            hash = 13 * hash + this.timeInCombat;
            hash = 13 * hash + this.respawnTimeSpent;
            hash = 13 * hash + (int) (this.damageTaken ^ (this.damageTaken >>> 32));
            hash = 13 * hash + this.meleeHits;
            hash = 13 * hash + this.jumps;
            hash = 13 * hash + Objects.hashCode(this.mobKills);
            hash = 13 * hash + Objects.hashCode(this.mobAssists);
            hash = 13 * hash + Objects.hashCode(this.mobDeaths);
            return hash;
        }
    }
}
