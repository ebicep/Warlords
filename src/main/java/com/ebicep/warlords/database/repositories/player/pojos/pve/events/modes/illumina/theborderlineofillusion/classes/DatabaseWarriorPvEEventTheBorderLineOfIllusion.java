package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina.theborderlineofillusion.classes;


import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina.theborderlineofillusion.PvEEventIlluminaTheBorderLineOfIllusionStatsWarlordsSpecs;

public class DatabaseWarriorPvEEventTheBorderLineOfIllusion implements PvEEventIlluminaTheBorderLineOfIllusionStatsWarlordsSpecs {

    private DatabaseBasePvEEventTheBorderLineOfIllusion berserker = new DatabaseBasePvEEventTheBorderLineOfIllusion();
    private DatabaseBasePvEEventTheBorderLineOfIllusion defender = new DatabaseBasePvEEventTheBorderLineOfIllusion();
    private DatabaseBasePvEEventTheBorderLineOfIllusion revenant = new DatabaseBasePvEEventTheBorderLineOfIllusion();

    public DatabaseWarriorPvEEventTheBorderLineOfIllusion() {
        super();
    }

    @Override
    public DatabaseBasePvEEventTheBorderLineOfIllusion[] getSpecs() {
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
