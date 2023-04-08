package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

public class DrippingGloves extends SpecialGammaGauntlet implements EPSandEPH, CraftsInto.CraftsPalmOfTheSoothsayer {

    @Override
    public String getName() {
        return "Dripping Gloves";
    }

    @Override
    public String getBonus() {
        return "+5 EPS but -20% EPH";
    }

    @Override
    public String getDescription() {
        return "It seems that air drying isn't very effective.";
    }

    @Override
    public Classes getClasses() {
        return Classes.MAGE;
    }

}
