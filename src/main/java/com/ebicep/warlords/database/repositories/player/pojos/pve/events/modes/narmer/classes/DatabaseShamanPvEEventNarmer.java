package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.DatabaseBasePvEEventNarmer;

import java.util.List;

public class DatabaseShamanPvEEventNarmer extends DatabaseBasePvEEventNarmer implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventNarmer thunderlord = new DatabaseBasePvEEventNarmer();
    private DatabaseBasePvEEventNarmer spiritguard = new DatabaseBasePvEEventNarmer();
    private DatabaseBasePvEEventNarmer earthwarden = new DatabaseBasePvEEventNarmer();

    public DatabaseShamanPvEEventNarmer() {
        super();
    }

    @Override
    public List<List> getSpecs() {
        return new DatabaseBasePvEEventNarmer[]{thunderlord, spiritguard, earthwarden};
    }

    public DatabaseBasePvEEventNarmer getThunderlord() {
        return thunderlord;
    }

    public DatabaseBasePvEEventNarmer getSpiritguard() {
        return spiritguard;
    }

    public DatabaseBasePvEEventNarmer getEarthwarden() {
        return earthwarden;
    }

}
