
package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltarobonanza.classes;


import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltarobonanza.DatabaseBasePvEEventBoltaroBonanza;

public class DatabaseDruidPvEEventBoltaroBonanza extends DatabaseBasePvEEventBoltaroBonanza implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventBoltaroBonanza conjurer = new DatabaseBasePvEEventBoltaroBonanza();
    private DatabaseBasePvEEventBoltaroBonanza guardian = new DatabaseBasePvEEventBoltaroBonanza();
    private DatabaseBasePvEEventBoltaroBonanza priest = new DatabaseBasePvEEventBoltaroBonanza();

    public DatabaseDruidPvEEventBoltaroBonanza() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBasePvEEventBoltaroBonanza[]{conjurer, guardian, priest};
    }


    public DatabaseBasePvEEventBoltaroBonanza getConjurer() {
        return conjurer;
    }

    public DatabaseBasePvEEventBoltaroBonanza getGuardian() {
        return guardian;
    }

    public DatabaseBasePvEEventBoltaroBonanza getPriest() {
        return priest;
    }

}
