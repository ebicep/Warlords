package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.gamma;

import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

import java.util.Set;

public class MediatorsFist extends SpecialGammaGauntlet implements EPSandEPH, CraftsInto.CraftsSamsonsFists {

    public MediatorsFist() {
    }

    public MediatorsFist(Set<BasicStatPool> basicStatPools) {
        super(basicStatPools);
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
