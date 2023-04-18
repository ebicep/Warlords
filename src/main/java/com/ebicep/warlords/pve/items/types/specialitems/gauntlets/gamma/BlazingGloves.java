package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

public class BlazingGloves extends SpecialGammaGauntlet implements EPSandMaxEnergy, CraftsInto.CraftsPalmOfTheSoothsayer {


    @Override
    public String getName() {
        return "Blazing Gloves";
    }

    @Override
    public String getBonus() {
        return "Increase energy gain by 3 per second but reduces energy cap by 20%";
    }

    @Override
    public String getDescription() {
        return "It seems you have spontaneously combusted.";
    }

    @Override
    public Classes getClasses() {
        return Classes.MAGE;
    }

}
