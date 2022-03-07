package com.ebicep.warlords.player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

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
    private transient Entry total = null;

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
        public float getRespawnTimeSpent() {
            return respawnTimeSpent;
        }

        @Override
        public String toString() {
            return "{"
                    + "kills=" + kills
                    + ", assists=" + assists
                    + ", deaths=" + deaths
                    + ", damage=" + damage
                    + ", healing=" + healing
                    + ", absorbed=" + absorbed
                    + ", damageOnCarrier=" + damageOnCarrier
                    + ", healingOnCarrier=" + healingOnCarrier
                    + ", flagsCaptured=" + flagsCaptured
                    + ", flagsReturned=" + flagsReturned
                    + ", timeInCombat=" + timeInCombat
                    + ", respawnTimeSpent=" + respawnTimeSpent
                    + '}';
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
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Entry other = (Entry) obj;
            return this.kills == other.kills
                    && this.assists == other.assists
                    && this.deaths == other.deaths
                    && this.damage == other.damage
                    && this.healing == other.healing
                    && this.absorbed == other.absorbed
                    && this.damageOnCarrier == other.damageOnCarrier
                    && this.healingOnCarrier == other.healingOnCarrier
                    && this.flagsCaptured == other.flagsCaptured
                    && this.flagsReturned == other.flagsReturned
                    && this.timeInCombat == other.timeInCombat
                    && this.respawnTimeSpent == other.respawnTimeSpent;
        }
    }
}
