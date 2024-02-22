package com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense;

import com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense.classes.*;
import com.ebicep.warlords.player.general.Classes;

public class DatabasePlayerPvEWaveDefensePlayerCountStats implements WaveDefenseStatsWarlordsClasses {

    private DatabaseMagePvEWaveDefense mage = new DatabaseMagePvEWaveDefense();
    private DatabaseWarriorPvEWaveDefense warrior = new DatabaseWarriorPvEWaveDefense();
    private DatabasePaladinPvEWaveDefense paladin = new DatabasePaladinPvEWaveDefense();
    private DatabaseShamanPvEWaveDefense shaman = new DatabaseShamanPvEWaveDefense();
    private DatabaseRoguePvEWaveDefense rogue = new DatabaseRoguePvEWaveDefense();
    private DatabaseArcanistPvEWaveDefense arcanist = new DatabaseArcanistPvEWaveDefense();

    @Override
    public WaveDefenseStatsWarlordsSpecs getClass(Classes classes) {
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
