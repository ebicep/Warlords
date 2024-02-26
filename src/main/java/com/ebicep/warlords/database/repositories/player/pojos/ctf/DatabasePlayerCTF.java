package com.ebicep.warlords.database.repositories.player.pojos.ctf;

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
    public CTFStatsWarlordsSpecs getClass(Classes classes) {
        return switch (classes) {
            case MAGE -> mage;
            case WARRIOR -> warrior;
            case PALADIN -> paladin;
            case SHAMAN -> shaman;
            case ROGUE -> rogue;
            case ARCANIST -> arcanist;
        };
    }

}
