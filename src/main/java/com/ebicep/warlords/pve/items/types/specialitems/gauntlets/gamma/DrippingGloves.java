package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.gamma;

import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

import java.util.Set;

public class DrippingGloves extends SpecialGammaGauntlet implements CraftsInto.CraftsPalmOfTheSoothsayer {

    public DrippingGloves() {
    }

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
    public Specializations getSpec() {
        return Specializations.AQUAMANCER;
    }
}
