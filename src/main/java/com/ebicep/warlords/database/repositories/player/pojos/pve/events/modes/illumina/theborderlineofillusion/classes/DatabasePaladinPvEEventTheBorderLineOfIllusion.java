package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina.theborderlineofillusion.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina.theborderlineofillusion.DatabaseBasePvEEventTheBorderLineOfIllusion;

public class DatabasePaladinPvEEventTheBorderLineOfIllusion extends DatabaseBasePvEEventTheBorderLineOfIllusion implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventTheBorderLineOfIllusion avenger = new DatabaseBasePvEEventTheBorderLineOfIllusion();
    private DatabaseBasePvEEventTheBorderLineOfIllusion crusader = new DatabaseBasePvEEventTheBorderLineOfIllusion();
    private DatabaseBasePvEEventTheBorderLineOfIllusion protector = new DatabaseBasePvEEventTheBorderLineOfIllusion();

    public DatabasePaladinPvEEventTheBorderLineOfIllusion() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
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
