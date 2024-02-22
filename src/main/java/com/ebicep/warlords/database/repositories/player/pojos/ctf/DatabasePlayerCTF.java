package com.ebicep.warlords.database.repositories.player.pojos.ctf;

import com.ebicep.warlords.database.repositories.games.pojos.ctf.DatabaseGameCTF;
import com.ebicep.warlords.database.repositories.games.pojos.ctf.DatabaseGamePlayerCTF;
import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.ctf.classses.*;
import com.ebicep.warlords.player.general.Classes;

public class DatabasePlayerCTF implements CTFStatsWarlordsClasses {

    private DatabaseMageCTF mage = new DatabaseMageCTF();
    private DatabaseWarriorCTF warrior = new DatabaseWarriorCTF();
    private DatabasePaladinCTF paladin = new DatabasePaladinCTF();
    private DatabaseShamanCTF shaman = new DatabaseShamanCTF();
    private DatabaseRogueCTF rogue = new DatabaseRogueCTF();
    private DatabaseArcanistCTF arcanist = new DatabaseArcanistCTF();

    @Override
    public StatsWarlordsSpecs<DatabaseGameCTF, DatabaseGamePlayerCTF, CTFStats> getClass(Classes classes) {
        return switch (classes) {
            case MAGE -> getMage();
            case WARRIOR -> getWarrior();
            case PALADIN -> getPaladin();
            case SHAMAN -> getShaman();
            case ROGUE -> getRogue();
            case ARCANIST -> getArcanist();
        };
    }
}
