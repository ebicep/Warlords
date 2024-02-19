package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.spidersdwelling.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.spidersdwelling.DatabaseBasePvEEventSpidersDwelling;

public class DatabaseRoguePvEEventSpidersDwelling extends DatabaseBasePvEEventSpidersDwelling implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventSpidersDwelling assassin = new DatabaseBasePvEEventSpidersDwelling();
    private DatabaseBasePvEEventSpidersDwelling vindicator = new DatabaseBasePvEEventSpidersDwelling();
    private DatabaseBasePvEEventSpidersDwelling apothecary = new DatabaseBasePvEEventSpidersDwelling();

    public DatabaseRoguePvEEventSpidersDwelling() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
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
