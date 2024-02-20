package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.spidersdwelling.classes;


import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.spidersdwelling.DatabaseBasePvEEventSpidersDwelling;

import java.util.List;

public class DatabaseWarriorPvEEventSpidersDwelling extends DatabaseBasePvEEventSpidersDwelling implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventSpidersDwelling berserker = new DatabaseBasePvEEventSpidersDwelling();
    private DatabaseBasePvEEventSpidersDwelling defender = new DatabaseBasePvEEventSpidersDwelling();
    private DatabaseBasePvEEventSpidersDwelling revenant = new DatabaseBasePvEEventSpidersDwelling();

    public DatabaseWarriorPvEEventSpidersDwelling() {
        super();
    }

    @Override
    public List<List> getSpecs() {
        return new DatabaseBasePvEEventSpidersDwelling[]{berserker, defender, revenant};
    }


    public DatabaseBasePvEEventSpidersDwelling getBerserker() {
        return berserker;
    }

    public DatabaseBasePvEEventSpidersDwelling getDefender() {
        return defender;
    }

    public DatabaseBasePvEEventSpidersDwelling getRevenant() {
        return revenant;
    }

}
