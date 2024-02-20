package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.theacropolis.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.theacropolis.DatabaseBasePvEEventGardenOfHesperidesAcropolis;

import java.util.List;

public class DatabaseRoguePvEEventGardenOfHesperidesAcropolis extends DatabaseBasePvEEventGardenOfHesperidesAcropolis implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventGardenOfHesperidesAcropolis assassin = new DatabaseBasePvEEventGardenOfHesperidesAcropolis();
    private DatabaseBasePvEEventGardenOfHesperidesAcropolis vindicator = new DatabaseBasePvEEventGardenOfHesperidesAcropolis();
    private DatabaseBasePvEEventGardenOfHesperidesAcropolis apothecary = new DatabaseBasePvEEventGardenOfHesperidesAcropolis();

    public DatabaseRoguePvEEventGardenOfHesperidesAcropolis() {
        super();
    }

    @Override
    public List<List> getSpecs() {
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
