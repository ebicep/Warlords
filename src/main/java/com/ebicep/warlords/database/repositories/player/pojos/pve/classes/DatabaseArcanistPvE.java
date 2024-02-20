
package com.ebicep.warlords.database.repositories.player.pojos.pve.classes;

import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePlayerPvEBase;
import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePvEBase;
import com.ebicep.warlords.database.repositories.player.pojos.pve.PvEStatsWarlordsSpecs;

public class DatabaseArcanistPvE implements PvEStatsWarlordsSpecs<DatabaseGamePvEBase, DatabaseGamePlayerPvEBase, DatabaseBasePvE> {

    protected DatabaseBasePvE conjurer = new DatabaseBasePvE();
    protected DatabaseBasePvE sentinel = new DatabaseBasePvE();
    protected DatabaseBasePvE luminary = new DatabaseBasePvE();

    public DatabaseArcanistPvE() {
        super();
    }

    @Override
    public DatabaseBasePvE[] getSpecs() {
        return new DatabaseBasePvE[]{conjurer, sentinel, luminary};
    }

    public DatabaseBasePvE getConjurer() {
        return conjurer;
    }

    public DatabaseBasePvE getSentinel() {
        return sentinel;
    }

    public DatabaseBasePvE getLuminary() {
        return luminary;
    }

}
