package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.tartarus.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.tartarus.DatabaseBasePvEEventGardenOfHesperidesTartarus;

import java.util.List;

public class DatabasePaladinPvEEventGardenOfHesperidesTartarus extends DatabaseBasePvEEventGardenOfHesperidesTartarus implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventGardenOfHesperidesTartarus avenger = new DatabaseBasePvEEventGardenOfHesperidesTartarus();
    private DatabaseBasePvEEventGardenOfHesperidesTartarus crusader = new DatabaseBasePvEEventGardenOfHesperidesTartarus();
    private DatabaseBasePvEEventGardenOfHesperidesTartarus protector = new DatabaseBasePvEEventGardenOfHesperidesTartarus();

    public DatabasePaladinPvEEventGardenOfHesperidesTartarus() {
        super();
    }

    @Override
    public List<List> getSpecs() {
        return new DatabaseBasePvEEventGardenOfHesperidesTartarus[]{avenger, crusader, protector};
    }

    public DatabaseBasePvEEventGardenOfHesperidesTartarus getAvenger() {
        return avenger;
    }

    public DatabaseBasePvEEventGardenOfHesperidesTartarus getCrusader() {
        return crusader;
    }

    public DatabaseBasePvEEventGardenOfHesperidesTartarus getProtector() {
        return protector;
    }

}
