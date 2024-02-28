package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltaroslair.classes;


import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltaroslair.PvEEventBoltaroLairStatsWarlordsSpecs;

public class DatabaseWarriorPvEEventBoltaroLair implements PvEEventBoltaroLairStatsWarlordsSpecs {

    private DatabaseBasePvEEventBoltaroLair berserker = new DatabaseBasePvEEventBoltaroLair();
    private DatabaseBasePvEEventBoltaroLair defender = new DatabaseBasePvEEventBoltaroLair();
    private DatabaseBasePvEEventBoltaroLair revenant = new DatabaseBasePvEEventBoltaroLair();

    public DatabaseWarriorPvEEventBoltaroLair() {
        super();
    }

    @Override
    public DatabaseBasePvEEventBoltaroLair[] getSpecs() {
        return new DatabaseBasePvEEventBoltaroLair[]{berserker, defender, revenant};
    }


    public DatabaseBasePvEEventBoltaroLair getBerserker() {
        return berserker;
    }

    public DatabaseBasePvEEventBoltaroLair getDefender() {
        return defender;
    }

    public DatabaseBasePvEEventBoltaroLair getRevenant() {
        return revenant;
    }

}
