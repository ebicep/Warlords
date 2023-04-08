package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

public class ToxicKnuckles extends SpecialGammaGauntlet implements EPSandMaxEnergy, CraftsInto.CraftsMultipurposeKnuckles {

    @Override
    public Classes getClasses() {
        return Classes.ROGUE;
    }

    @Override
    public String getName() {
        return "Toxic Knuckles";
    }

    @Override
    public String getBonus() {
        return "+5 EPS but -20% Max NRG.";
    }

    @Override
    public String getDescription() {
        return "Bites like a snake.";
    }

}
