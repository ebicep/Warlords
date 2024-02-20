package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina.theborderlineofillusion.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina.theborderlineofillusion.DatabaseBasePvEEventTheBorderLineOfIllusion;

import java.util.List;

public class DatabaseRoguePvEEventTheBorderLineOfIllusion extends DatabaseBasePvEEventTheBorderLineOfIllusion implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventTheBorderLineOfIllusion assassin = new DatabaseBasePvEEventTheBorderLineOfIllusion();
    private DatabaseBasePvEEventTheBorderLineOfIllusion vindicator = new DatabaseBasePvEEventTheBorderLineOfIllusion();
    private DatabaseBasePvEEventTheBorderLineOfIllusion apothecary = new DatabaseBasePvEEventTheBorderLineOfIllusion();

    public DatabaseRoguePvEEventTheBorderLineOfIllusion() {
        super();
    }

    @Override
    public List<List> getSpecs() {
        return new DatabaseBasePvEEventTheBorderLineOfIllusion[]{assassin, vindicator, apothecary};
    }


    public DatabaseBasePvEEventTheBorderLineOfIllusion getAssassin() {
        return assassin;
    }

    public DatabaseBasePvEEventTheBorderLineOfIllusion getVindicator() {
        return vindicator;
    }

    public DatabaseBasePvEEventTheBorderLineOfIllusion getApothecary() {
        return apothecary;
    }
}
