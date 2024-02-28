package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.spidersdwelling.classes;

import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.spidersdwelling.PvEEventMithraSpidersDwellingStatsWarlordsSpecs;

public class DatabaseShamanPvEEventMithraSpidersDwelling implements PvEEventMithraSpidersDwellingStatsWarlordsSpecs {

    private DatabaseBasePvEEventMithraSpidersDwelling thunderlord = new DatabaseBasePvEEventMithraSpidersDwelling();
    private DatabaseBasePvEEventMithraSpidersDwelling spiritguard = new DatabaseBasePvEEventMithraSpidersDwelling();
    private DatabaseBasePvEEventMithraSpidersDwelling earthwarden = new DatabaseBasePvEEventMithraSpidersDwelling();

    public DatabaseShamanPvEEventMithraSpidersDwelling() {
        super();
    }

    @Override
    public DatabaseBasePvEEventMithraSpidersDwelling[] getSpecs() {
        return new DatabaseBasePvEEventMithraSpidersDwelling[]{thunderlord, spiritguard, earthwarden};
    }

    public DatabaseBasePvEEventMithraSpidersDwelling getThunderlord() {
        return thunderlord;
    }

    public DatabaseBasePvEEventMithraSpidersDwelling getSpiritguard() {
        return spiritguard;
    }

    public DatabaseBasePvEEventMithraSpidersDwelling getEarthwarden() {
        return earthwarden;
    }

}
