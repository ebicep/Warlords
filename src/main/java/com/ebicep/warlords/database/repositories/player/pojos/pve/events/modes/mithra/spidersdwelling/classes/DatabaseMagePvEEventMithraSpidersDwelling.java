package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.spidersdwelling.classes;

import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.spidersdwelling.PvEEventMithraSpidersDwellingStatsWarlordsSpecs;

public class DatabaseMagePvEEventMithraSpidersDwelling implements PvEEventMithraSpidersDwellingStatsWarlordsSpecs {

    protected DatabaseBasePvEEventMithraSpidersDwelling pyromancer = new DatabaseBasePvEEventMithraSpidersDwelling();
    protected DatabaseBasePvEEventMithraSpidersDwelling cryomancer = new DatabaseBasePvEEventMithraSpidersDwelling();
    protected DatabaseBasePvEEventMithraSpidersDwelling aquamancer = new DatabaseBasePvEEventMithraSpidersDwelling();

    public DatabaseMagePvEEventMithraSpidersDwelling() {
        super();
    }

    @Override
    public DatabaseBasePvEEventMithraSpidersDwelling[] getSpecs() {
        return new DatabaseBasePvEEventMithraSpidersDwelling[]{pyromancer, cryomancer, aquamancer};
    }

    public DatabaseBasePvEEventMithraSpidersDwelling getPyromancer() {
        return pyromancer;
    }

    public DatabaseBasePvEEventMithraSpidersDwelling getCryomancer() {
        return cryomancer;
    }

    public DatabaseBasePvEEventMithraSpidersDwelling getAquamancer() {
        return aquamancer;
    }

}
