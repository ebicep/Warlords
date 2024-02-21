package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.theacropolis.classes;

import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.theacropolis.PvEEventGardenOfHesperidesTheAcropolisStatsWarlordsSpecs;

public class DatabaseRoguePvEEventGardenOfHesperidesAcropolis implements PvEEventGardenOfHesperidesTheAcropolisStatsWarlordsSpecs {

    private DatabaseBasePvEEventGardenOfHesperidesAcropolis assassin = new DatabaseBasePvEEventGardenOfHesperidesAcropolis();
    private DatabaseBasePvEEventGardenOfHesperidesAcropolis vindicator = new DatabaseBasePvEEventGardenOfHesperidesAcropolis();
    private DatabaseBasePvEEventGardenOfHesperidesAcropolis apothecary = new DatabaseBasePvEEventGardenOfHesperidesAcropolis();

    public DatabaseRoguePvEEventGardenOfHesperidesAcropolis() {
        super();
    }

    @Override
    public DatabaseBasePvEEventGardenOfHesperidesAcropolis[] getSpecs() {
        return new DatabaseBasePvEEventGardenOfHesperidesAcropolis[]{assassin, vindicator, apothecary};
    }


    public DatabaseBasePvEEventGardenOfHesperidesAcropolis getAssassin() {
        return assassin;
    }

    public DatabaseBasePvEEventGardenOfHesperidesAcropolis getVindicator() {
        return vindicator;
    }

    public DatabaseBasePvEEventGardenOfHesperidesAcropolis getApothecary() {
        return apothecary;
    }
}
