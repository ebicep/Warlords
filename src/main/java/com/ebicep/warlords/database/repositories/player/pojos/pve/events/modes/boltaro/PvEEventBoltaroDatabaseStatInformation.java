package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.DatabaseGamePlayerPvEEventBoltaro;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.DatabaseGamePvEEventBoltaro;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.game.GameMode;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.LinkedHashMap;
import java.util.Map;

public class PvEEventBoltaroDatabaseStatInformation extends AbstractDatabaseStatInformation {

    @Field("experience_pve")
    protected long experiencePvE;
    @Field("total_time_played")
    protected long totalTimePlayed = 0;
    @Field("mob_kills")
    protected Map<String, Long> mobKills = new LinkedHashMap<>();
    @Field("mob_assists")
    protected Map<String, Long> mobAssists = new LinkedHashMap<>();
    @Field("mob_deaths")
    protected Map<String, Long> mobDeaths = new LinkedHashMap<>();

    @Field("event_points_cum")
    private long eventPointsCumulative;
    @Field("event_points")
    private long eventPoints;
    @Field("highest_split")
    private int highestSplit;

    @Override
    public void updateCustomStats(
            DatabaseGameBase databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerBase gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        assert databaseGame instanceof DatabaseGamePvEEventBoltaro;
        assert gamePlayer instanceof DatabaseGamePlayerPvEEventBoltaro;

        DatabaseGamePvEEventBoltaro databaseGamePvEEventBoltaro = (DatabaseGamePvEEventBoltaro) databaseGame;
        DatabaseGamePlayerPvEEventBoltaro databaseGamePlayerPvEEventBoltaro = (DatabaseGamePlayerPvEEventBoltaro) gamePlayer;

        this.totalTimePlayed += (long) databaseGamePvEEventBoltaro.getTimeElapsed() * multiplier;
        databaseGamePlayerPvEEventBoltaro.getMobKills().forEach((s, aLong) -> this.mobKills.merge(s, aLong * multiplier, Long::sum));
        databaseGamePlayerPvEEventBoltaro.getMobAssists().forEach((s, aLong) -> this.mobAssists.merge(s, aLong * multiplier, Long::sum));
        databaseGamePlayerPvEEventBoltaro.getMobDeaths().forEach((s, aLong) -> this.mobDeaths.merge(s, aLong * multiplier, Long::sum));

        this.eventPointsCumulative += databaseGamePlayerPvEEventBoltaro.getPoints() * multiplier;
        this.eventPoints += databaseGamePlayerPvEEventBoltaro.getPoints() * multiplier;
        int split = databaseGamePvEEventBoltaro.getHighestSplit();
        if (multiplier > 0) {
            if (this.highestSplit < split) {
                this.highestSplit = split;
            }
        }
    }

    public long getExperiencePvE() {
        return experiencePvE;
    }

    public long getTotalTimePlayed() {
        return totalTimePlayed;
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

    public long getEventPointsCumulative() {
        return eventPointsCumulative;
    }

    public long getEventPoints() {
        return eventPoints;
    }

    public int getHighestSplit() {
        return highestSplit;
    }
}
