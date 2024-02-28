package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.tartarus.classes;

import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.tartarus.PvEEventGardenOfHesperidesTartarusStatsWarlordsSpecs;

public class DatabaseWarriorPvEEventGardenOfHesperidesTartarus implements PvEEventGardenOfHesperidesTartarusStatsWarlordsSpecs {

    private DatabaseBasePvEEventGardenOfHesperidesTartarus berserker = new DatabaseBasePvEEventGardenOfHesperidesTartarus();
    private DatabaseBasePvEEventGardenOfHesperidesTartarus defender = new DatabaseBasePvEEventGardenOfHesperidesTartarus();
    private DatabaseBasePvEEventGardenOfHesperidesTartarus revenant = new DatabaseBasePvEEventGardenOfHesperidesTartarus();

    public DatabaseWarriorPvEEventGardenOfHesperidesTartarus() {
        super();
    }

    @Override
    public DatabaseBasePvEEventGardenOfHesperidesTartarus[] getSpecs() {
        return new DatabaseBasePvEEventGardenOfHesperidesTartarus[]{berserker, defender, revenant};
    }

    public DatabaseBasePvEEventGardenOfHesperidesTartarus getBerserker() {
        return berserker;
    }

    public DatabaseBasePvEEventGardenOfHesperidesTartarus getDefender() {
        return defender;
    }

    public DatabaseBasePvEEventGardenOfHesperidesTartarus getRevenant() {
        return revenant;
    }

}
