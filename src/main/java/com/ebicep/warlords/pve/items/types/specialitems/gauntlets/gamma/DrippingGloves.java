package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

import java.util.Set;

public class DrippingGloves extends SpecialGammaGauntlet implements EPSandEPH, CraftsInto.CraftsPalmOfTheSoothsayer {

    public DrippingGloves(Set<BasicStatPool> basicStatPools) {
        super(basicStatPools);
    }

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
