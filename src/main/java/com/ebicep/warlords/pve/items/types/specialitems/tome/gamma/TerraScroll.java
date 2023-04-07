package com.ebicep.warlords.pve.items.types.specialitems.tome.gamma;

import com.ebicep.warlords.player.general.Classes;

public class TerraScroll extends SpecialGammaTome implements CDRandHealing {

    @Override
    public String getName() {
        return "Terra Scroll";
    }

    @Override
    public String getBonus() {
        return "+5% Cooldown Reduction but -20% Healing.";
    }

    @Override
    public String getDescription() {
        return "Unlock the mysteries of the ground...";
    }

    @Override
    public Classes getClasses() {
        return Classes.SHAMAN;
    }


}
