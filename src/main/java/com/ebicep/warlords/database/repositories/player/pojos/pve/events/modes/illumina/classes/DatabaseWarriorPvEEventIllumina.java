package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina.classes;


import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina.DatabaseBasePvEEventIllumina;

import java.util.List;

public class DatabaseWarriorPvEEventIllumina extends DatabaseBasePvEEventIllumina implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventIllumina berserker = new DatabaseBasePvEEventIllumina();
    private DatabaseBasePvEEventIllumina defender = new DatabaseBasePvEEventIllumina();
    private DatabaseBasePvEEventIllumina revenant = new DatabaseBasePvEEventIllumina();

    public DatabaseWarriorPvEEventIllumina() {
        super();
    }

    @Override
    public List<List> getSpecs() {
        return new DatabaseBasePvEEventIllumina[]{berserker, defender, revenant};
    }


    public DatabaseBasePvEEventIllumina getBerserker() {
        return berserker;
    }

    public DatabaseBasePvEEventIllumina getDefender() {
        return defender;
    }

    public DatabaseBasePvEEventIllumina getRevenant() {
        return revenant;
    }

}
