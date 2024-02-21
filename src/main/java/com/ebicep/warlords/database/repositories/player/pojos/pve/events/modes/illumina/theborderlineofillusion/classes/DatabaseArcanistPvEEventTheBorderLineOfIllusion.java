package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina.theborderlineofillusion.classes;


import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina.theborderlineofillusion.PvEEventIlluminaTheBorderLineOfIllusionStatsWarlordsSpecs;

public class DatabaseArcanistPvEEventTheBorderLineOfIllusion implements PvEEventIlluminaTheBorderLineOfIllusionStatsWarlordsSpecs {

    private DatabaseBasePvEEventTheBorderLineOfIllusion conjurer = new DatabaseBasePvEEventTheBorderLineOfIllusion();
    private DatabaseBasePvEEventTheBorderLineOfIllusion sentinel = new DatabaseBasePvEEventTheBorderLineOfIllusion();
    private DatabaseBasePvEEventTheBorderLineOfIllusion luminary = new DatabaseBasePvEEventTheBorderLineOfIllusion();

    public DatabaseArcanistPvEEventTheBorderLineOfIllusion() {
        super();
    }

    @Override
    public DatabaseBasePvEEventTheBorderLineOfIllusion[] getSpecs() {
        return new DatabaseBasePvEEventTheBorderLineOfIllusion[]{conjurer, sentinel, luminary};
    }


    public DatabaseBasePvEEventTheBorderLineOfIllusion getConjurer() {
        return conjurer;
    }

    public DatabaseBasePvEEventTheBorderLineOfIllusion getSentinel() {
        return sentinel;
    }

    public DatabaseBasePvEEventTheBorderLineOfIllusion getLuminary() {
        return luminary;
    }

}
