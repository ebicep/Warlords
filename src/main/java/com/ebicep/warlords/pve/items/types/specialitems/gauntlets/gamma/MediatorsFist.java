package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

public class MediatorsFist extends SpecialGammaGauntlet implements EPSandEPH, CraftsInto.CraftsSamsonsFists {

    @Override
    public Classes getClasses() {
        return Classes.PALADIN;
    }

    @Override
    public String getName() {
        return "Mediator's Fist";
    }

    @Override
    public String getBonus() {
        return "+5 EPS but -20% EPH";
    }

    @Override
    public String getDescription() {
        return "Fear not! I am risen.";
    }

}
