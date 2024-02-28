package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.tartarus.classes;

import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.tartarus.PvEEventGardenOfHesperidesTartarusStatsWarlordsSpecs;

public class DatabaseShamanPvEEventGardenOfHesperidesTartarus implements PvEEventGardenOfHesperidesTartarusStatsWarlordsSpecs {

    private DatabaseBasePvEEventGardenOfHesperidesTartarus thunderlord = new DatabaseBasePvEEventGardenOfHesperidesTartarus();
    private DatabaseBasePvEEventGardenOfHesperidesTartarus spiritguard = new DatabaseBasePvEEventGardenOfHesperidesTartarus();
    private DatabaseBasePvEEventGardenOfHesperidesTartarus earthwarden = new DatabaseBasePvEEventGardenOfHesperidesTartarus();

    public DatabaseShamanPvEEventGardenOfHesperidesTartarus() {
        super();
    }

    @Override
    public DatabaseBasePvEEventGardenOfHesperidesTartarus[] getSpecs() {
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
