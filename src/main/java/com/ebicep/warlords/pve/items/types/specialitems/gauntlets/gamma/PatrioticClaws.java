package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.gamma;

import com.ebicep.warlords.player.general.Classes;

public class PatrioticClaws extends SpecialGammaGauntlet implements EPSandSpeed {

    @Override
    public Classes getClasses() {
        return Classes.WARRIOR;
    }

    @Override
    public String getName() {
        return "Patriotic Claws";
    }

    @Override
    public String getBonus() {
        return "+5 EPS but -20% Speed.";
    }

    @Override
    public String getDescription() {
        return "Only someone in the depths of despair would accept such a gift.";
    }

}
