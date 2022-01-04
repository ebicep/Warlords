package com.ebicep.warlords.database.repositories.player.pojos;

public class AbstractDatabaseStatInformation {

    protected int kills = 0;
    protected int assists = 0;
    protected int deaths = 0;
    protected int wins = 0;
    protected int losses = 0;
    protected int plays = 0;
    protected long damage = 0;
    protected long healing = 0;
    protected long absorbed = 0;
    protected long experience = 0;

    public AbstractDatabaseStatInformation() {
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

    public int getPlays() {
        return plays;
    }

    public void setPlays(int plays) {
        this.plays = plays;
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

    public long getExperience() {
        return experience;
    }

    public void setExperience(long experience) {
        this.experience = experience;
    }
}
