package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.spidersdwelling.classes;


import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.spidersdwelling.PvEEventMithraSpidersDwellingStatsWarlordsSpecs;

public class DatabaseWarriorPvEEventMithraSpidersDwelling implements PvEEventMithraSpidersDwellingStatsWarlordsSpecs {

    private DatabaseBasePvEEventMithraSpidersDwelling berserker = new DatabaseBasePvEEventMithraSpidersDwelling();
    private DatabaseBasePvEEventMithraSpidersDwelling defender = new DatabaseBasePvEEventMithraSpidersDwelling();
    private DatabaseBasePvEEventMithraSpidersDwelling revenant = new DatabaseBasePvEEventMithraSpidersDwelling();

    public DatabaseWarriorPvEEventMithraSpidersDwelling() {
        super();
    }

    @Override
    public DatabaseBasePvEEventMithraSpidersDwelling[] getSpecs() {
        return new DatabaseBasePvEEventMithraSpidersDwelling[]{berserker, defender, revenant};
    }


    public DatabaseBasePvEEventMithraSpidersDwelling getBerserker() {
        return berserker;
    }

    public DatabaseBasePvEEventMithraSpidersDwelling getDefender() {
        return defender;
    }

    public DatabaseBasePvEEventMithraSpidersDwelling getRevenant() {
        return revenant;
    }

}
