
package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.theacropolis.classes;


import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.theacropolis.PvEEventGardenOfHesperidesTheAcropolisStatsWarlordsSpecs;

public class DatabaseArcanistPvEEventGardenOfHesperidesAcropolis implements PvEEventGardenOfHesperidesTheAcropolisStatsWarlordsSpecs {

    private DatabaseBasePvEEventGardenOfHesperidesAcropolis conjurer = new DatabaseBasePvEEventGardenOfHesperidesAcropolis();
    private DatabaseBasePvEEventGardenOfHesperidesAcropolis sentinel = new DatabaseBasePvEEventGardenOfHesperidesAcropolis();
    private DatabaseBasePvEEventGardenOfHesperidesAcropolis luminary = new DatabaseBasePvEEventGardenOfHesperidesAcropolis();

    public DatabaseArcanistPvEEventGardenOfHesperidesAcropolis() {
        super();
    }

    @Override
    public DatabaseBasePvEEventGardenOfHesperidesAcropolis[] getSpecs() {
        return new DatabaseBasePvEEventGardenOfHesperidesAcropolis[]{conjurer, sentinel, luminary};
    }


    public DatabaseBasePvEEventGardenOfHesperidesAcropolis getConjurer() {
        return conjurer;
    }

    public DatabaseBasePvEEventGardenOfHesperidesAcropolis getSentinel() {
        return sentinel;
    }

    public DatabaseBasePvEEventGardenOfHesperidesAcropolis getLuminary() {
        return luminary;
    }

}
