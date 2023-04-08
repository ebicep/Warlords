package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

public class HerbalKnuckles extends SpecialGammaGauntlet implements EPSandEPH, CraftsInto.CraftsMultipurposeKnuckles {

    @Override
    public Classes getClasses() {
        return Classes.ROGUE;
    }

    @Override
    public String getName() {
        return "Herbal Knuckles";
    }

    @Override
    public String getBonus() {
        return "+5 EPS but -20% EPH";
    }

    @Override
    public String getDescription() {
        return "Cuts like a rose.";
    }

}
