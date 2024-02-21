package com.ebicep.warlords.database.repositories.player.pojos.pve.events.classes;

import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePlayerPvEEvent;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePvEEvent;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.PvEEventStatsWarlordsSpecs;

public class DatabaseRoguePvEEvent implements PvEEventStatsWarlordsSpecs<DatabaseGamePvEEvent, DatabaseGamePlayerPvEEvent, DatabaseBasePvEEvent> {

    private DatabaseBasePvEEvent assassin = new DatabaseBasePvEEvent();
    private DatabaseBasePvEEvent vindicator = new DatabaseBasePvEEvent();
    private DatabaseBasePvEEvent apothecary = new DatabaseBasePvEEvent();

    public DatabaseRoguePvEEvent() {
        super();
    }

    @Override
    public DatabaseBasePvEEvent[] getSpecs() {
        return new DatabaseBasePvEEvent[]{assassin, vindicator, apothecary};
    }


    public DatabaseBasePvEEvent getAssassin() {
        return assassin;
    }

    public DatabaseBasePvEEvent getVindicator() {
        return vindicator;
    }

    public DatabaseBasePvEEvent getApothecary() {
        return apothecary;
    }
}
