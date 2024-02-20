package com.ebicep.warlords.database.repositories.player.pojos;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.ExperienceManager;

public abstract class AbstractDatabaseStatInformation<T extends DatabaseGameBase, R extends DatabaseGamePlayerBase> implements Stats<T, R> {

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

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer,
            T databaseGame,
            GameMode gameMode,
            R gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
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
        this.experience += gamePlayer.getExperienceEarnedSpec() * multiplier;
    }

    @Override
    public int getKills() {
        return kills;
    }

    @Override
    public int getAssists() {
        return assists;
    }

    @Override
    public int getDeaths() {
        return deaths;
    }

    @Override
    public int getWins() {
        return wins;
    }

    @Override
    public int getLosses() {
        return losses;
    }

    @Override
    public int getPlays() {
        return plays;
    }

    @Override
    public long getDamage() {
        return damage;
    }

    @Override
    public long getHealing() {
        return healing;
    }

    @Override
    public long getAbsorbed() {
        return absorbed;
    }

    @Override
    public long getExperience() {
        return experience;
    }

    @Override
    public void setExperience(long experience) {
        this.experience = experience;
    }

    public int getLevel() {
        return ExperienceManager.getLevelFromExp(experience);
    }

}
