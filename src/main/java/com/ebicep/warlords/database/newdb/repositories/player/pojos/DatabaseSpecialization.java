package com.ebicep.warlords.database.newdb.repositories.player.pojos;

import com.ebicep.warlords.player.Weapons;

public class DatabaseSpecialization {

    private int kills = 0;
    private int assists = 0;
    private int deaths = 0;
    private int wins = 0;
    private int losses = 0;
    private int flagsCaptured = 0;
    private int flagsReturned = 0;
    private long damage = 0;
    private long healing = 0;
    private long absorbed = 0;
    private Weapons weapon = Weapons.FELFLAME_BLADE;
    private long experience = 0;

    public DatabaseSpecialization() {

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

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
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

    public Weapons getWeapon() {
        return weapon;
    }

    public void setWeapon(Weapons weapon) {
        this.weapon = weapon;
    }

    public long getExperience() {
        return experience;
    }

    public void setExperience(long experience) {
        this.experience = experience;
    }
}
