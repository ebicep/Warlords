package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.classes;


import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.DatabaseBasePvEEventNarmer;

public class DatabaseWarriorPvEEventNarmer extends DatabaseBasePvEEventNarmer implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventNarmer berserker = new DatabaseBasePvEEventNarmer();
    private DatabaseBasePvEEventNarmer defender = new DatabaseBasePvEEventNarmer();
    private DatabaseBasePvEEventNarmer revenant = new DatabaseBasePvEEventNarmer();

    public DatabaseWarriorPvEEventNarmer() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
        return new DatabaseBasePvEEventNarmer[]{berserker, defender, revenant};
    }


    public DatabaseBasePvEEventNarmer getBerserker() {
        return berserker;
    }

    public DatabaseBasePvEEventNarmer getDefender() {
        return defender;
    }

    public DatabaseBasePvEEventNarmer getRevenant() {
        return revenant;
    }

}
