package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltarobonanza.classes;


import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltarobonanza.PvEEventBoltaroBonanzaStatsWarlordsSpecs;

public class DatabaseWarriorPvEEventBoltaroBonanza implements PvEEventBoltaroBonanzaStatsWarlordsSpecs {

    private DatabaseBasePvEEventBoltaroBonanza berserker = new DatabaseBasePvEEventBoltaroBonanza();
    private DatabaseBasePvEEventBoltaroBonanza defender = new DatabaseBasePvEEventBoltaroBonanza();
    private DatabaseBasePvEEventBoltaroBonanza revenant = new DatabaseBasePvEEventBoltaroBonanza();

    public DatabaseWarriorPvEEventBoltaroBonanza() {
        super();
    }

    @Override
    public DatabaseBasePvEEventBoltaroBonanza[] getSpecs() {
        return new DatabaseBasePvEEventBoltaroBonanza[]{berserker, defender, revenant};
    }


    public DatabaseBasePvEEventBoltaroBonanza getBerserker() {
        return berserker;
    }

    public DatabaseBasePvEEventBoltaroBonanza getDefender() {
        return defender;
    }

    public DatabaseBasePvEEventBoltaroBonanza getRevenant() {
        return revenant;
    }

}
