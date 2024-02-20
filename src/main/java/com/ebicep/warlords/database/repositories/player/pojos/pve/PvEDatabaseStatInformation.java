package com.ebicep.warlords.database.repositories.player.pojos.pve;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePlayerPvEBase;
import com.ebicep.warlords.database.repositories.games.pojos.pve.MostDamageInRound;
import com.ebicep.warlords.database.repositories.games.pojos.pve.TimeElapsed;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameMode;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.LinkedHashMap;
import java.util.Map;

public class PvEDatabaseStatInformation extends AbstractDatabaseStatInformation implements PvEStats {

    //CUMULATIVE STATS
    @Field("total_time_played")
    protected long totalTimePlayed = 0;
    @Field("mob_kills")
    protected Map<String, Long> mobKills = new LinkedHashMap<>();
    @Field("mob_assists")
    protected Map<String, Long> mobAssists = new LinkedHashMap<>();
    @Field("mob_deaths")
    protected Map<String, Long> mobDeaths = new LinkedHashMap<>();
// TODO
//TOP STATS
    @Field("most_damage_in_round")
    protected long mostDamageInRound;


    public PvEDatabaseStatInformation() {
    }

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer, DatabaseGameBase databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerBase gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        assert gamePlayer instanceof DatabaseGamePlayerPvEBase;

        DatabaseGamePlayerPvEBase gamePlayerPvEBase = (DatabaseGamePlayerPvEBase) gamePlayer;
        gamePlayerPvEBase.getMobKills().forEach((s, aLong) -> this.mobKills.merge(s, aLong * multiplier, Long::sum));
        gamePlayerPvEBase.getMobAssists().forEach((s, aLong) -> this.mobAssists.merge(s, aLong * multiplier, Long::sum));
        gamePlayerPvEBase.getMobDeaths().forEach((s, aLong) -> this.mobDeaths.merge(s, aLong * multiplier, Long::sum));

        if (gamePlayer instanceof MostDamageInRound mostDamageInRound) {
            if (multiplier > 0) {
                this.mostDamageInRound = Math.max(this.mostDamageInRound, mostDamageInRound.getMostDamageInRound());
            } else if (this.mostDamageInRound == mostDamageInRound.getMostDamageInRound()) {
                this.mostDamageInRound = 0;
            }
        }

        if (databaseGame instanceof TimeElapsed timeElapsed) {
            this.totalTimePlayed += (long) timeElapsed.getTimeElapsed() * multiplier;
        }
    }

    public void merge(PvEDatabaseStatInformation other) {
        super.merge(other);
        this.totalTimePlayed += other.totalTimePlayed;
        this.mostDamageInRound = Math.max(this.mostDamageInRound, other.mostDamageInRound);
        other.mobKills.forEach((s, aLong) -> this.mobKills.merge(s, aLong, Long::sum));
        other.mobAssists.forEach((s, aLong) -> this.mobAssists.merge(s, aLong, Long::sum));
        other.mobDeaths.forEach((s, aLong) -> this.mobDeaths.merge(s, aLong, Long::sum));

    }

    public long getTotalTimePlayed() {
        return totalTimePlayed;
    }

    @Override
    public Map<String, Long> getMobKills() {
        return mobKills;
    }

    @Override
    public Map<String, Long> getMobAssists() {
        return mobAssists;
    }

    @Override
    public Map<String, Long> getMobDeaths() {
        return mobDeaths;
    }

//    @Override
//    public long getMostDamageInRound() {
//        return mostDamageInRound;
//    }
//
//    @Override
//    public void setMostDamageInRound(long mostDamageInRound) {
//        this.mostDamageInRound = mostDamageInRound;
//    }

    public void addTimePlayed(long time) {
        this.totalTimePlayed += time;
    }

}
