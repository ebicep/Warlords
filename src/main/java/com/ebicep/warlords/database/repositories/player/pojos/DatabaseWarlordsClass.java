package com.ebicep.warlords.database.repositories.player.pojos;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayers;
import org.springframework.data.mongodb.core.mapping.Field;

public abstract class DatabaseWarlordsClass {

    @Field
    protected int kills = 0;
    protected int assists = 0;
    protected int deaths = 0;
    protected int wins = 0;
    protected int losses = 0;
    protected int plays = 0;
    @Field("flags_captured")
    protected int flagsCaptured = 0;
    @Field("flags_returned")
    protected int flagsReturned = 0;
    protected long damage = 0;
    protected long healing = 0;
    protected long absorbed = 0;
    protected long experience = 0;

    public DatabaseWarlordsClass() {
    }

    public abstract DatabaseSpecialization[] getSpecs();

    public void updateStats(DatabaseGamePlayers.GamePlayer gamePlayer, boolean won, boolean add) {
        int operation = add ? 1 : -1;
        this.kills += gamePlayer.getTotalKills() * operation;
        this.assists += gamePlayer.getTotalAssists() * operation;
        this.deaths += gamePlayer.getTotalDeaths() * operation;
        if (won) {
            this.wins += operation;
        } else {
            this.losses += operation;
        }
        this.plays += operation;
        this.flagsCaptured += gamePlayer.getFlagCaptures() * operation;
        this.flagsReturned += gamePlayer.getFlagReturns() * operation;
        this.damage += gamePlayer.getTotalDamage() * operation;
        this.healing += gamePlayer.getTotalHealing() * operation;
        this.absorbed += gamePlayer.getTotalAbsorbed() * operation;
        this.experience += gamePlayer.getExperienceEarnedSpec() * operation;
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

    public long getExperience() {
        return experience;
    }

    public void setExperience(long experience) {
        this.experience = experience;
    }

    //    private static BasicDBObject getBaseSpecStats() {
//        return new BasicDBObject("kills", 0)
//                .append("assists", 0)
//                .append("deaths", 0)
//                .append("wins", 0)
//                .append("losses", 0)
//                .append("flags_captured", 0)
//                .append("flags_returned", 0)
//                .append("damage", 0L)
//                .append("healing", 0L)
//                .append("absorbed", 0L);
//    }
}
