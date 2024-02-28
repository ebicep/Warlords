package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.spidersdwelling.classes;

import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.spidersdwelling.PvEEventMithraSpidersDwellingStatsWarlordsSpecs;

public class DatabasePaladinPvEEventMithraSpidersDwelling implements PvEEventMithraSpidersDwellingStatsWarlordsSpecs {

    private DatabaseBasePvEEventMithraSpidersDwelling avenger = new DatabaseBasePvEEventMithraSpidersDwelling();
    private DatabaseBasePvEEventMithraSpidersDwelling crusader = new DatabaseBasePvEEventMithraSpidersDwelling();
    private DatabaseBasePvEEventMithraSpidersDwelling protector = new DatabaseBasePvEEventMithraSpidersDwelling();

    public DatabasePaladinPvEEventMithraSpidersDwelling() {
        super();
    }

    @Override
    public DatabaseBasePvEEventMithraSpidersDwelling[] getSpecs() {
        return new DatabaseBasePvEEventMithraSpidersDwelling[]{avenger, crusader, protector};
    }

    public DatabaseBasePvEEventMithraSpidersDwelling getAvenger() {
        return avenger;
    }

    public DatabaseBasePvEEventMithraSpidersDwelling getCrusader() {
        return crusader;
    }

    public DatabaseBasePvEEventMithraSpidersDwelling getProtector() {
        return protector;
    }

}
