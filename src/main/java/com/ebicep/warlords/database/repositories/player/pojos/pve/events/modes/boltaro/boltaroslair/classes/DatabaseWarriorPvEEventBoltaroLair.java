package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltaroslair.classes;


import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltaroslair.DatabaseBasePvEEventBoltaroLair;

import java.util.List;

public class DatabaseWarriorPvEEventBoltaroLair extends DatabaseBasePvEEventBoltaroLair implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventBoltaroLair berserker = new DatabaseBasePvEEventBoltaroLair();
    private DatabaseBasePvEEventBoltaroLair defender = new DatabaseBasePvEEventBoltaroLair();
    private DatabaseBasePvEEventBoltaroLair revenant = new DatabaseBasePvEEventBoltaroLair();

    public DatabaseWarriorPvEEventBoltaroLair() {
        super();
    }

    @Override
    public List<List> getSpecs() {
        return new DatabaseBasePvEEventBoltaroLair[]{berserker, defender, revenant};
    }


    public DatabaseBasePvEEventBoltaroLair getBerserker() {
        return berserker;
    }

    public DatabaseBasePvEEventBoltaroLair getDefender() {
        return defender;
    }

    public DatabaseBasePvEEventBoltaroLair getRevenant() {
        return revenant;
    }

}
