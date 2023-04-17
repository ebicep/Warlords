package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltarobonanza.classes;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltarobonanza.DatabaseBasePvEEventBoltaroBonanza;

public class DatabasePaladinPvEEventBoltaroBonanza extends DatabaseBasePvEEventBoltaroBonanza implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventBoltaroBonanza avenger = new DatabaseBasePvEEventBoltaroBonanza();
    private DatabaseBasePvEEventBoltaroBonanza crusader = new DatabaseBasePvEEventBoltaroBonanza();
    private DatabaseBasePvEEventBoltaroBonanza protector = new DatabaseBasePvEEventBoltaroBonanza();

    public DatabasePaladinPvEEventBoltaroBonanza() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBasePvEEventBoltaroBonanza[]{avenger, crusader, protector};
    }

    public DatabaseBasePvEEventBoltaroBonanza getAvenger() {
        return avenger;
    }

    public DatabaseBasePvEEventBoltaroBonanza getCrusader() {
        return crusader;
    }

    public DatabaseBasePvEEventBoltaroBonanza getProtector() {
        return protector;
    }

}
