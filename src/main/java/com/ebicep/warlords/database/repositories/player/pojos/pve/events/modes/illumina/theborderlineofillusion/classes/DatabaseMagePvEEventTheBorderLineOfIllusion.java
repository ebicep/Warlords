package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina.theborderlineofillusion.classes;

import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina.theborderlineofillusion.PvEEventIlluminaTheBorderLineOfIllusionStatsWarlordsSpecs;

public class DatabaseMagePvEEventTheBorderLineOfIllusion implements PvEEventIlluminaTheBorderLineOfIllusionStatsWarlordsSpecs {

    protected DatabaseBasePvEEventTheBorderLineOfIllusion pyromancer = new DatabaseBasePvEEventTheBorderLineOfIllusion();
    protected DatabaseBasePvEEventTheBorderLineOfIllusion cryomancer = new DatabaseBasePvEEventTheBorderLineOfIllusion();
    protected DatabaseBasePvEEventTheBorderLineOfIllusion aquamancer = new DatabaseBasePvEEventTheBorderLineOfIllusion();

    public DatabaseMagePvEEventTheBorderLineOfIllusion() {
        super();
    }

    @Override
    public DatabaseBasePvEEventTheBorderLineOfIllusion[] getSpecs() {
        return new DatabaseBasePvEEventTheBorderLineOfIllusion[]{pyromancer, cryomancer, aquamancer};
    }

    public DatabaseBasePvEEventTheBorderLineOfIllusion getPyromancer() {
        return pyromancer;
    }

    public DatabaseBasePvEEventTheBorderLineOfIllusion getCryomancer() {
        return cryomancer;
    }

    public DatabaseBasePvEEventTheBorderLineOfIllusion getAquamancer() {
        return aquamancer;
    }

}
