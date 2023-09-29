package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.gamma;

import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

import java.util.Set;

public class VoltageGauntlet extends SpecialGammaGauntlet implements EPSandMaxEnergy, CraftsInto.CraftsGardeningGloves {

    public VoltageGauntlet() {
    }

    public VoltageGauntlet(Set<BasicStatPool> basicStatPools) {
        super(basicStatPools);
    }


    @Override
    public String getName() {
        return "Voltage Gauntlet";
    }

    @Override
    public String getBonus() {
        return "+5 EPS but -20% Max NRG.";
    }

    @Override
    public String getDescription() {
        return "One touch and you're toast.";
    }

}
