package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.tartarus.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.tartarus.DatabaseBasePvEEventGardenOfHesperidesTartarus;

public class DatabaseShamanPvEEventGardenOfHesperidesTartarus extends DatabaseBasePvEEventGardenOfHesperidesTartarus implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventGardenOfHesperidesTartarus thunderlord = new DatabaseBasePvEEventGardenOfHesperidesTartarus();
    private DatabaseBasePvEEventGardenOfHesperidesTartarus spiritguard = new DatabaseBasePvEEventGardenOfHesperidesTartarus();
    private DatabaseBasePvEEventGardenOfHesperidesTartarus earthwarden = new DatabaseBasePvEEventGardenOfHesperidesTartarus();

    public DatabaseShamanPvEEventGardenOfHesperidesTartarus() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
        return new DatabaseBasePvEEventGardenOfHesperidesTartarus[]{thunderlord, spiritguard, earthwarden};
    }

    public DatabaseBasePvEEventGardenOfHesperidesTartarus getThunderlord() {
        return thunderlord;
    }

    public DatabaseBasePvEEventGardenOfHesperidesTartarus getSpiritguard() {
        return spiritguard;
    }

    public DatabaseBasePvEEventGardenOfHesperidesTartarus getEarthwarden() {
        return earthwarden;
    }

}
