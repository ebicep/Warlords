package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina.theborderlineofillusion.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina.theborderlineofillusion.DatabaseBasePvEEventTheBorderLineOfIllusion;

import java.util.List;

public class DatabaseMagePvEEventTheBorderLineOfIllusion extends DatabaseBasePvEEventTheBorderLineOfIllusion implements DatabaseWarlordsSpecs {

    protected DatabaseBasePvEEventTheBorderLineOfIllusion pyromancer = new DatabaseBasePvEEventTheBorderLineOfIllusion();
    protected DatabaseBasePvEEventTheBorderLineOfIllusion cryomancer = new DatabaseBasePvEEventTheBorderLineOfIllusion();
    protected DatabaseBasePvEEventTheBorderLineOfIllusion aquamancer = new DatabaseBasePvEEventTheBorderLineOfIllusion();

    public DatabaseMagePvEEventTheBorderLineOfIllusion() {
        super();
    }

    @Override
    public List<List> getSpecs() {
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
