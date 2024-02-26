
package com.ebicep.warlords.database.repositories.player.pojos.pve.onslaught.classes;

import com.ebicep.warlords.database.repositories.player.pojos.pve.onslaught.OnslaughtStatsWarlordsSpecs;

public class DatabaseArcanistPvEOnslaught implements OnslaughtStatsWarlordsSpecs {

    private DatabaseBasePvEOnslaught conjurer = new DatabaseBasePvEOnslaught();
    private DatabaseBasePvEOnslaught sentinel = new DatabaseBasePvEOnslaught();
    private DatabaseBasePvEOnslaught luminary = new DatabaseBasePvEOnslaught();

    public DatabaseArcanistPvEOnslaught() {
        super();
    }

    @Override
    public DatabaseBasePvEOnslaught[] getSpecs() {
        return new DatabaseBasePvEOnslaught[]{conjurer, sentinel, luminary};
    }

    public DatabaseBasePvEOnslaught getConjurer() {
        return conjurer;
    }

    public DatabaseBasePvEOnslaught getSentinel() {
        return sentinel;
    }

    public DatabaseBasePvEOnslaught getLuminary() {
        return luminary;
    }
}
