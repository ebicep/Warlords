package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.narmerstomb.classes;


import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.narmerstomb.DatabaseBasePvEEventNarmersTomb;

import java.util.List;

public class DatabaseWarriorPvEEventNarmersTomb extends DatabaseBasePvEEventNarmersTomb implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventNarmersTomb berserker = new DatabaseBasePvEEventNarmersTomb();
    private DatabaseBasePvEEventNarmersTomb defender = new DatabaseBasePvEEventNarmersTomb();
    private DatabaseBasePvEEventNarmersTomb revenant = new DatabaseBasePvEEventNarmersTomb();

    public DatabaseWarriorPvEEventNarmersTomb() {
        super();
    }

    @Override
    public List<List> getSpecs() {
        return new DatabaseBasePvEEventNarmersTomb[]{berserker, defender, revenant};
    }


    public DatabaseBasePvEEventNarmersTomb getBerserker() {
        return berserker;
    }

    public DatabaseBasePvEEventNarmersTomb getDefender() {
        return defender;
    }

    public DatabaseBasePvEEventNarmersTomb getRevenant() {
        return revenant;
    }

}
