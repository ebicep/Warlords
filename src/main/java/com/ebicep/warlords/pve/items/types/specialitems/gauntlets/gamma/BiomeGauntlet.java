package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.gamma;

import com.ebicep.warlords.player.general.Classes;

public class BiomeGauntlet extends SpecialGammaGauntlet implements EPSandEPH {

    @Override
    public Classes getClasses() {
        return Classes.SHAMAN;
    }

    @Override
    public String getName() {
        return "Biome Gauntlet";
    }

    @Override
    public String getBonus() {
        return "+5 EPS but -20% EPH";
    }

    @Override
    public String getDescription() {
        return "One touch and you're underground.";
    }

}
