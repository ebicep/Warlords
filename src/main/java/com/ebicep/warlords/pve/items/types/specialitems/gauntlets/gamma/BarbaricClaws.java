package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

public class BarbaricClaws extends SpecialGammaGauntlet implements EPSandMaxEnergy, CraftsInto.CraftsPendragonGauntlets {

    @Override
    public Classes getClasses() {
        return Classes.WARRIOR;
    }

    @Override
    public String getName() {
        return "Barbaric Claws";
    }

    @Override
    public String getBonus() {
        return "+5 EPS but -20% Max NRG.";
    }

    @Override
    public String getDescription() {
        return "Only a madman would wear such an instrument.";
    }

}
