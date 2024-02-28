package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.spidersdwelling.classes;


import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.spidersdwelling.PvEEventMithraSpidersDwellingStatsWarlordsSpecs;

public class DatabaseArcanistPvEEventMithraSpidersDwelling implements PvEEventMithraSpidersDwellingStatsWarlordsSpecs {

    private DatabaseBasePvEEventMithraSpidersDwelling conjurer = new DatabaseBasePvEEventMithraSpidersDwelling();
    private DatabaseBasePvEEventMithraSpidersDwelling sentinel = new DatabaseBasePvEEventMithraSpidersDwelling();
    private DatabaseBasePvEEventMithraSpidersDwelling luminary = new DatabaseBasePvEEventMithraSpidersDwelling();

    public DatabaseArcanistPvEEventMithraSpidersDwelling() {
        super();
    }

    @Override
    public DatabaseBasePvEEventMithraSpidersDwelling[] getSpecs() {
        return new DatabaseBasePvEEventMithraSpidersDwelling[]{conjurer, sentinel, luminary};
    }


    public DatabaseBasePvEEventMithraSpidersDwelling getConjurer() {
        return conjurer;
    }

    public DatabaseBasePvEEventMithraSpidersDwelling getSentinel() {
        return sentinel;
    }

    public DatabaseBasePvEEventMithraSpidersDwelling getLuminary() {
        return luminary;
    }

}
