package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.gamma;

import com.ebicep.warlords.player.general.Classes;

public class BluntKnuckles extends SpecialGammaGauntlet implements EPSandSpeed {

    @Override
    public Classes getClasses() {
        return Classes.ROGUE;
    }

    @Override
    public String getName() {
        return "Blunt Knuckles";
    }

    @Override
    public String getBonus() {
        return "+5 EPS but -20% Speed";
    }

    @Override
    public String getDescription() {
        return "Hits like a truck.";
    }

}
