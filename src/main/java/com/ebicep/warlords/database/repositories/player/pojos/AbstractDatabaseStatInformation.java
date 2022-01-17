package com.ebicep.warlords.database.repositories.player.pojos;

import com.ebicep.warlords.database.repositories.games.GameMode;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGame;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayers;

public abstract class AbstractDatabaseStatInformation {

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

    public void updateStats(GameMode gameMode, boolean isCompGame, DatabaseGame databaseGame, DatabaseGamePlayers.GamePlayer gamePlayer, boolean won, boolean add) {
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
        this.damage += gamePlayer.getTotalDamage() * operation;
        this.healing += gamePlayer.getTotalHealing() * operation;
        this.absorbed += gamePlayer.getTotalAbsorbed() * operation;
        this.updateCustomStats(gameMode, isCompGame, databaseGame, gamePlayer, won, add);
    }

    public abstract void updateCustomStats(GameMode gameMode, boolean isCompGame, DatabaseGame databaseGame, DatabaseGamePlayers.GamePlayer gamePlayer, boolean won, boolean add);

    public double getKDA() {
        if (deaths == 0) {
            return 0;
        }
        return (kills + assists) / (double) deaths;
    }

    public double getKillsPerGame() {
        return plays == 0 ? 0 : (double) kills / plays;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public double getKillsAssistsPerGame() {
        return plays == 0 ? 0 : (double) (kills + assists) / plays;
    }

    public int getAssists() {
        return assists;
    }

    public void setAssists(int assists) {
        this.assists = assists;
    }

    public double getDeathsPerGame() {
        return plays == 0 ? 0 : (double) deaths / plays;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public double getWL() {
        if (losses == 0) {
            return 0;
        }
        return (double) wins / losses;
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

    public long getDHP() {
        return damage + healing + absorbed;
    }

    public long getDHPPerGame() {
        return plays == 0 ? 0 : (damage + healing + absorbed) / (wins + losses);
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
