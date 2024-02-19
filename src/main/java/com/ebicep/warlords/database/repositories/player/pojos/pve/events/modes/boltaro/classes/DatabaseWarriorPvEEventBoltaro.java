package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.classes;


import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.DatabaseBasePvEEventBoltaro;

public class DatabaseWarriorPvEEventBoltaro extends DatabaseBasePvEEventBoltaro implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventBoltaro berserker = new DatabaseBasePvEEventBoltaro();
    private DatabaseBasePvEEventBoltaro defender = new DatabaseBasePvEEventBoltaro();
    private DatabaseBasePvEEventBoltaro revenant = new DatabaseBasePvEEventBoltaro();

    public DatabaseWarriorPvEEventBoltaro() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
        return new DatabaseBasePvEEventBoltaro[]{berserker, defender, revenant};
    }


    public DatabaseBasePvEEventBoltaro getBerserker() {
        return berserker;
    }

    public DatabaseBasePvEEventBoltaro getDefender() {
        return defender;
    }

    public DatabaseBasePvEEventBoltaro getRevenant() {
        return revenant;
    }

}
