package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.spidersdwelling.classes;

import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.spidersdwelling.PvEEventMithraSpidersDwellingStatsWarlordsSpecs;

public class DatabaseRoguePvEEventMithraSpidersDwelling implements PvEEventMithraSpidersDwellingStatsWarlordsSpecs {

    private DatabaseBasePvEEventMithraSpidersDwelling assassin = new DatabaseBasePvEEventMithraSpidersDwelling();
    private DatabaseBasePvEEventMithraSpidersDwelling vindicator = new DatabaseBasePvEEventMithraSpidersDwelling();
    private DatabaseBasePvEEventMithraSpidersDwelling apothecary = new DatabaseBasePvEEventMithraSpidersDwelling();

    public DatabaseRoguePvEEventMithraSpidersDwelling() {
        super();
    }

    @Override
    public DatabaseBasePvEEventMithraSpidersDwelling[] getSpecs() {
        return new DatabaseBasePvEEventMithraSpidersDwelling[]{assassin, vindicator, apothecary};
    }


    public DatabaseBasePvEEventMithraSpidersDwelling getAssassin() {
        return assassin;
    }

    public DatabaseBasePvEEventMithraSpidersDwelling getVindicator() {
        return vindicator;
    }

    public DatabaseBasePvEEventMithraSpidersDwelling getApothecary() {
        return apothecary;
    }
}
