package com.ebicep.warlords.player;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

public class PlayerStatistics {

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

    public PlayerStatistics(int expectedGameDurationInMinutes) {
        entries = new ArrayList<>(expectedGameDurationInMinutes);
        entries.add(current);
    }

    public void advanceMinute() {
        current = new Entry();
        entries.add(current);
    }

    public void addKill() {
        current.kills++;
        this.total = null;
    }

    public void addAssist() {
        current.assists++;
        this.total = null;
    }

    public void addDeath() {
        current.deaths++;
        this.total = null;
    }

    public void addDamage(long damage) {
        current.damage += damage;
        this.total = null;
    }

    public void addHealing(long healing) {
        current.healing += healing;
        this.total = null;
    }

    public void addAbsorbed(long absorbed) {
        current.absorbed += absorbed;
        this.total = null;
    }

    public void addDamageOnCarrier(long damageOnCarrier) {
        current.damageOnCarrier += damageOnCarrier;
        this.total = null;
    }

    public void addHealingOnCarrier(long healingOnCarrier) {
        current.healingOnCarrier += healingOnCarrier;
        this.total = null;
    }

    public void addFlagCapture() {
        current.flagsCaptured++;
        this.total = null;
    }

    public void addFlagReturned() {
        current.flagsReturned++;
        this.total = null;
    }

    @Nonnull
    public Entry total() {
        if (total != null) {
            total = entries.stream().reduce(new Entry(), Entry::sum);
        }
        return total;
    }

    @Nonnull
    public List<Entry> getEntries() {
        return entries;
    }

    public static class Entry {

        @Nonnegative
        public int kills;
        @Nonnegative
        public int assists;

        @Nonnegative
        public int deaths;
        @Nonnegative
        public long damage;
        @Nonnegative
        public long healing;
        @Nonnegative
        public long absorbed;

        @Nonnegative
        public long damageOnCarrier;
        @Nonnegative
        public long healingOnCarrier;

        @Nonnegative
        public int flagsCaptured;
        @Nonnegative
        public int flagsReturned;

        public Entry sum(Entry other) {
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
            return this;
        }

        public int getKills() {
            return kills;
        }

        public void setKills(int kills) {
            this.kills = kills;
        }

        public int getAssists() {
            return assists;
        }

        public void setAssists(int assists) {
            this.assists = assists;
        }

        public int getDeaths() {
            return deaths;
        }

        public void setDeaths(int deaths) {
            this.deaths = deaths;
        }

        public long getDamage() {
            return damage;
        }

        public void setDamage(long damage) {
            this.damage = damage;
        }

        public long getHealing() {
            return healing;
        }

        public void setHealing(long healing) {
            this.healing = healing;
        }

        public long getAbsorbed() {
            return absorbed;
        }

        public void setAbsorbed(long absorbed) {
            this.absorbed = absorbed;
        }

        public long getDamageOnCarrier() {
            return damageOnCarrier;
        }

        public void setDamageOnCarrier(long damageOnCarrier) {
            this.damageOnCarrier = damageOnCarrier;
        }

        public long getHealingOnCarrier() {
            return healingOnCarrier;
        }

        public void setHealingOnCarrier(long healingOnCarrier) {
            this.healingOnCarrier = healingOnCarrier;
        }

        public int getFlagsCaptured() {
            return flagsCaptured;
        }

        public void setFlagsCaptured(int flagsCaptured) {
            this.flagsCaptured = flagsCaptured;
        }

        public int getFlagsReturned() {
            return flagsReturned;
        }

        public void setFlagsReturned(int flagsReturned) {
            this.flagsReturned = flagsReturned;
        }
    }
}
