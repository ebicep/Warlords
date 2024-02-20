package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.spidersdwelling.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.spidersdwelling.DatabaseBasePvEEventSpidersDwelling;

import java.util.List;

public class DatabaseRoguePvEEventSpidersDwelling extends DatabaseBasePvEEventSpidersDwelling implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventSpidersDwelling assassin = new DatabaseBasePvEEventSpidersDwelling();
    private DatabaseBasePvEEventSpidersDwelling vindicator = new DatabaseBasePvEEventSpidersDwelling();
    private DatabaseBasePvEEventSpidersDwelling apothecary = new DatabaseBasePvEEventSpidersDwelling();

    public DatabaseRoguePvEEventSpidersDwelling() {
        super();
    }

    @Override
    public List<List> getSpecs() {
        return new DatabaseBasePvEEventSpidersDwelling[]{assassin, vindicator, apothecary};
    }


    public DatabaseBasePvEEventSpidersDwelling getAssassin() {
        return assassin;
    }

    public DatabaseBasePvEEventSpidersDwelling getVindicator() {
        return vindicator;
    }

    public DatabaseBasePvEEventSpidersDwelling getApothecary() {
        return apothecary;
    }
}
