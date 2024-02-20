package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina.theborderlineofillusion.classes;


import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina.theborderlineofillusion.DatabaseBasePvEEventTheBorderLineOfIllusion;

import java.util.List;

public class DatabaseWarriorPvEEventTheBorderLineOfIllusion extends DatabaseBasePvEEventTheBorderLineOfIllusion implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventTheBorderLineOfIllusion berserker = new DatabaseBasePvEEventTheBorderLineOfIllusion();
    private DatabaseBasePvEEventTheBorderLineOfIllusion defender = new DatabaseBasePvEEventTheBorderLineOfIllusion();
    private DatabaseBasePvEEventTheBorderLineOfIllusion revenant = new DatabaseBasePvEEventTheBorderLineOfIllusion();

    public DatabaseWarriorPvEEventTheBorderLineOfIllusion() {
        super();
    }

    @Override
    public List<List> getSpecs() {
        return new DatabaseBasePvEEventTheBorderLineOfIllusion[]{berserker, defender, revenant};
    }


    public DatabaseBasePvEEventTheBorderLineOfIllusion getBerserker() {
        return berserker;
    }

    public DatabaseBasePvEEventTheBorderLineOfIllusion getDefender() {
        return defender;
    }

    public DatabaseBasePvEEventTheBorderLineOfIllusion getRevenant() {
        return revenant;
    }

}
