package com.ebicep.warlords.database.repositories.player.pojos.pve.events.classes;


import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePlayerPvEEvent;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePvEEvent;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.PvEEventStatsWarlordsSpecs;

public class DatabaseWarriorPvEEvent implements PvEEventStatsWarlordsSpecs<DatabaseGamePvEEvent, DatabaseGamePlayerPvEEvent, DatabaseBasePvEEvent> {

    private DatabaseBasePvEEvent berserker = new DatabaseBasePvEEvent();
    private DatabaseBasePvEEvent defender = new DatabaseBasePvEEvent();
    private DatabaseBasePvEEvent revenant = new DatabaseBasePvEEvent();

    public DatabaseWarriorPvEEvent() {
        super();
    }

    @Override
    public DatabaseBasePvEEvent[] getSpecs() {
        return new DatabaseBasePvEEvent[]{berserker, defender, revenant};
    }

    public DatabaseBasePvEEvent getBerserker() {
        return berserker;
    }

    public DatabaseBasePvEEvent getDefender() {
        return defender;
    }

    public DatabaseBasePvEEvent getRevenant() {
        return revenant;
    }

}
