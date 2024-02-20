package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltarobonanza.classes;


import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltarobonanza.DatabaseBasePvEEventBoltaroBonanza;

import java.util.List;

public class DatabaseWarriorPvEEventBoltaroBonanza implements StatsWarlordsSpecs<DatabaseBasePvEEventBoltaroBonanza> {

    private DatabaseBasePvEEventBoltaroBonanza berserker = new DatabaseBasePvEEventBoltaroBonanza();
    private DatabaseBasePvEEventBoltaroBonanza defender = new DatabaseBasePvEEventBoltaroBonanza();
    private DatabaseBasePvEEventBoltaroBonanza revenant = new DatabaseBasePvEEventBoltaroBonanza();

    public DatabaseWarriorPvEEventBoltaroBonanza() {
        super();
    }

    @Override
    public List<List> getSpecs() {
        return new DatabaseBasePvEEventBoltaroBonanza[]{berserker, defender, revenant};
    }


    public DatabaseBasePvEEventBoltaroBonanza getBerserker() {
        return berserker;
    }

    public DatabaseBasePvEEventBoltaroBonanza getDefender() {
        return defender;
    }

    public DatabaseBasePvEEventBoltaroBonanza getRevenant() {
        return revenant;
    }

}
