package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.classes;


import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClass;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.DatabaseBasePvEEventNarmer;

public class DatabaseWarriorPvEEventNarmer extends DatabaseBasePvEEventNarmer implements DatabaseWarlordsClass {

    private DatabaseBasePvEEventNarmer berserker = new DatabaseBasePvEEventNarmer();
    private DatabaseBasePvEEventNarmer defender = new DatabaseBasePvEEventNarmer();
    private DatabaseBasePvEEventNarmer revenant = new DatabaseBasePvEEventNarmer();

    public DatabaseWarriorPvEEventNarmer() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
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
