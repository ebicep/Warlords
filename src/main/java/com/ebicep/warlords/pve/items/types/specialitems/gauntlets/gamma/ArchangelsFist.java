package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

public class ArchangelsFist extends SpecialGammaGauntlet implements EPSandMaxEnergy, CraftsInto.CraftsSamsonsFists {

    @Override
    public Classes getClasses() {
        return Classes.PALADIN;
    }

    @Override
    public String getName() {
        return "Archangel's Fist";
    }

    @Override
    public String getBonus() {
        return "Increase energy gain by 3 per second but reduces energy cap by 20%.";
    }

    @Override
    public String getDescription() {
        return "Behold! Judgement from on high!";
    }

}
