package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.narmerstomb.classes;


import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.narmerstomb.PvEEventNarmerNarmersTombStatsWarlordsSpecs;

public class DatabaseWarriorPvEEventNarmerNarmersTomb implements PvEEventNarmerNarmersTombStatsWarlordsSpecs {

    private DatabaseBasePvEEventNarmerNarmersTomb berserker = new DatabaseBasePvEEventNarmerNarmersTomb();
    private DatabaseBasePvEEventNarmerNarmersTomb defender = new DatabaseBasePvEEventNarmerNarmersTomb();
    private DatabaseBasePvEEventNarmerNarmersTomb revenant = new DatabaseBasePvEEventNarmerNarmersTomb();

    public DatabaseWarriorPvEEventNarmerNarmersTomb() {
        super();
    }

    @Override
    public DatabaseBasePvEEventNarmerNarmersTomb[] getSpecs() {
        return new DatabaseBasePvEEventNarmerNarmersTomb[]{berserker, defender, revenant};
    }


    public DatabaseBasePvEEventNarmerNarmersTomb getBerserker() {
        return berserker;
    }

    public DatabaseBasePvEEventNarmerNarmersTomb getDefender() {
        return defender;
    }

    public DatabaseBasePvEEventNarmerNarmersTomb getRevenant() {
        return revenant;
    }

}
