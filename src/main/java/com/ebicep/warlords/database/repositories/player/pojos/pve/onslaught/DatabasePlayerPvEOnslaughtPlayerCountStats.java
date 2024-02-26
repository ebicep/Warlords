package com.ebicep.warlords.database.repositories.player.pojos.pve.onslaught;

import com.ebicep.warlords.database.repositories.player.pojos.pve.onslaught.classes.*;
import com.ebicep.warlords.player.general.Classes;

public class DatabasePlayerPvEOnslaughtPlayerCountStats implements OnslaughtStatsWarlordsClasses {

    private DatabaseMagePvEOnslaught mage = new DatabaseMagePvEOnslaught();
    private DatabaseWarriorPvEOnslaught warrior = new DatabaseWarriorPvEOnslaught();
    private DatabasePaladinPvEOnslaught paladin = new DatabasePaladinPvEOnslaught();
    private DatabaseShamanPvEOnslaught shaman = new DatabaseShamanPvEOnslaught();
    private DatabaseRoguePvEOnslaught rogue = new DatabaseRoguePvEOnslaught();
    private DatabaseArcanistPvEOnslaught arcanist = new DatabaseArcanistPvEOnslaught();

    @Override
    public OnslaughtStatsWarlordsSpecs getClass(Classes classes) {
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
