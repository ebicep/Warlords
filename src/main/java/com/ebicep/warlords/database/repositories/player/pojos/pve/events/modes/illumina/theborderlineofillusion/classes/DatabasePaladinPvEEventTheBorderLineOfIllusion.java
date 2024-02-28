package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina.theborderlineofillusion.classes;

import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina.theborderlineofillusion.PvEEventIlluminaTheBorderLineOfIllusionStatsWarlordsSpecs;

public class DatabasePaladinPvEEventTheBorderLineOfIllusion implements PvEEventIlluminaTheBorderLineOfIllusionStatsWarlordsSpecs {

    private DatabaseBasePvEEventTheBorderLineOfIllusion avenger = new DatabaseBasePvEEventTheBorderLineOfIllusion();
    private DatabaseBasePvEEventTheBorderLineOfIllusion crusader = new DatabaseBasePvEEventTheBorderLineOfIllusion();
    private DatabaseBasePvEEventTheBorderLineOfIllusion protector = new DatabaseBasePvEEventTheBorderLineOfIllusion();

    public DatabasePaladinPvEEventTheBorderLineOfIllusion() {
        super();
    }

    @Override
    public DatabaseBasePvEEventTheBorderLineOfIllusion[] getSpecs() {
        return new DatabaseBasePvEEventTheBorderLineOfIllusion[]{avenger, crusader, protector};
    }

    public DatabaseBasePvEEventTheBorderLineOfIllusion getAvenger() {
        return avenger;
    }

    public DatabaseBasePvEEventTheBorderLineOfIllusion getCrusader() {
        return crusader;
    }

    public DatabaseBasePvEEventTheBorderLineOfIllusion getProtector() {
        return protector;
    }

}
