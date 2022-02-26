package com.ebicep.warlords.database.repositories.games.pojos.tdm;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import org.springframework.data.mongodb.core.mapping.Field;

public class DatabaseGameTDM extends DatabaseGameBase {

    @Field("blue_points")
    protected int bluePoints;
    @Field("red_points")
    protected int redPoints;

    public DatabaseGameTDM() {

    }

    @Override
    public String toString() {
        return "DatabaseGameTDM{" +
                "id='" + id + '\'' +
                ", exactDate=" + exactDate +
                ", date='" + date + '\'' +
                ", map=" + map +
                ", gameMode=" + gameMode +
                ", gameAddons=" + gameAddons +
                ", counted=" + counted +
                ", bluePoints=" + bluePoints +
                ", redPoints=" + redPoints +
                '}';
    }

    @Override
    public void updatePlayerStatsFromGame(DatabaseGameBase databaseGame, boolean add) {

    }

    @Override
    public DatabaseGamePlayerResult getPlayerGameResult(DatabaseGamePlayerBase player) {
        return null;
    }

    @Override
    public void createHolograms() {

    }

    @Override
    public String getGameLabel() {
        return "";
    }
}
