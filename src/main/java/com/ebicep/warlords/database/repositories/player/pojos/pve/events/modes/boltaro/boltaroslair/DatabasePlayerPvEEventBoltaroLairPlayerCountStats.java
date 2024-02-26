package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltaroslair;

import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltaroslair.classes.*;
import com.ebicep.warlords.player.general.Classes;

public class DatabasePlayerPvEEventBoltaroLairPlayerCountStats implements PvEEventBoltaroLairStatsWarlordsClasses {

    private DatabaseMagePvEEventBoltaroLair mage = new DatabaseMagePvEEventBoltaroLair();
    private DatabaseWarriorPvEEventBoltaroLair warrior = new DatabaseWarriorPvEEventBoltaroLair();
    private DatabasePaladinPvEEventBoltaroLair paladin = new DatabasePaladinPvEEventBoltaroLair();
    private DatabaseShamanPvEEventBoltaroLair shaman = new DatabaseShamanPvEEventBoltaroLair();
    private DatabaseRoguePvEEventBoltaroLair rogue = new DatabaseRoguePvEEventBoltaroLair();
    private DatabaseArcanistPvEEventBoltaroLair arcanist = new DatabaseArcanistPvEEventBoltaroLair();

    @Override
    public PvEEventBoltaroLairStatsWarlordsSpecs getClass(Classes classes) {
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
