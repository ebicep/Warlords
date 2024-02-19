package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.classes;


import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.DatabaseBasePvEEventMithra;

public class DatabaseWarriorPvEEventMithra extends DatabaseBasePvEEventMithra implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventMithra berserker = new DatabaseBasePvEEventMithra();
    private DatabaseBasePvEEventMithra defender = new DatabaseBasePvEEventMithra();
    private DatabaseBasePvEEventMithra revenant = new DatabaseBasePvEEventMithra();

    public DatabaseWarriorPvEEventMithra() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
        return new DatabaseBasePvEEventMithra[]{berserker, defender, revenant};
    }


    public DatabaseBasePvEEventMithra getBerserker() {
        return berserker;
    }

    public DatabaseBasePvEEventMithra getDefender() {
        return defender;
    }

    public DatabaseBasePvEEventMithra getRevenant() {
        return revenant;
    }

}
