package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina.theborderlineofillusion.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina.theborderlineofillusion.DatabaseBasePvEEventTheBorderLineOfIllusion;

public class DatabaseShamanPvEEventTheBorderLineOfIllusion extends DatabaseBasePvEEventTheBorderLineOfIllusion implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventTheBorderLineOfIllusion thunderlord = new DatabaseBasePvEEventTheBorderLineOfIllusion();
    private DatabaseBasePvEEventTheBorderLineOfIllusion spiritguard = new DatabaseBasePvEEventTheBorderLineOfIllusion();
    private DatabaseBasePvEEventTheBorderLineOfIllusion earthwarden = new DatabaseBasePvEEventTheBorderLineOfIllusion();

    public DatabaseShamanPvEEventTheBorderLineOfIllusion() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
        return new DatabaseBasePvEEventTheBorderLineOfIllusion[]{thunderlord, spiritguard, earthwarden};
    }

    public DatabaseBasePvEEventTheBorderLineOfIllusion getThunderlord() {
        return thunderlord;
    }

    public DatabaseBasePvEEventTheBorderLineOfIllusion getSpiritguard() {
        return spiritguard;
    }

    public DatabaseBasePvEEventTheBorderLineOfIllusion getEarthwarden() {
        return earthwarden;
    }

}
