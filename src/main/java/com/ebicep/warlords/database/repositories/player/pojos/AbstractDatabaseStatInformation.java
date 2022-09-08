package com.ebicep.warlords.database.repositories.player.pojos;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.ExperienceManager;

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

    public void updateStats(DatabaseGameBase databaseGame,
                            DatabaseGamePlayerBase gamePlayer,
                            int multiplier
    ) {
        DatabaseGamePlayerResult result = databaseGame.getPlayerGameResult(gamePlayer);
        this.kills += gamePlayer.getTotalKills() * multiplier;
        this.assists += gamePlayer.getTotalAssists() * multiplier;
        this.deaths += gamePlayer.getTotalDeaths() * multiplier;
        switch (result) {
            case WON:
                this.wins += multiplier;
                break;
            case LOST:
            case DRAW:
                this.losses += multiplier;
                break;
            case NONE:
                break;
        }
        this.plays += multiplier;
        this.damage += gamePlayer.getTotalDamage() * multiplier;
        this.healing += gamePlayer.getTotalHealing() * multiplier;
        this.absorbed += gamePlayer.getTotalAbsorbed() * multiplier;
        this.updateCustomStats(
                databaseGame,
                databaseGame.getGameMode(),
                gamePlayer,
                result,
                multiplier
        );
    }

    public abstract void updateCustomStats(
            DatabaseGameBase databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerBase gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier
    );

    public double getKDA() {
        if (deaths <= 0) {
            return 0;
        }
        return (kills + assists) / (double) deaths;
    }

    public double getKillsPerGame() {
        return plays <= 0 ? 0 : (double) kills / plays;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public double getKillsAssistsPerGame() {
        return plays <= 0 ? 0 : (double) (kills + assists) / plays;
    }

    public int getAssists() {
        return assists;
    }

    public void setAssists(int assists) {
        this.assists = assists;
    }

    public double getDeathsPerGame() {
        return plays <= 0 ? 0 : (double) deaths / plays;
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

    public double getWinRate() {
        if (plays == 0) {
            return 0;
        }
        return (double) wins / plays;
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
        return plays <= 0 ? 0 : (damage + healing + absorbed) / plays;
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

    public int getLevel() {
        return ExperienceManager.getLevelFromExp(experience);
    }

}
