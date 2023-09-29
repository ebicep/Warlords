package com.ebicep.warlords.pve.items.types.specialitems.tome.gamma;

import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

import java.util.Set;

public final class TomeOfWater extends SpecialGammaTome implements CraftsInto.CraftsFirewaterAlmanac {

    public TomeOfWater() {

    }

    public TomeOfWater(Set<BasicStatPool> basicStatPools) {
        super(basicStatPools);
    }

    @Override
    public String getName() {
        return "Tome of Water";
    }

    @Override
    public String getBonus() {
        return "+5% Cooldown Reduction but -20% Healing.";
    }

    @Override
    public String getDescription() {
        return "Step 1: Own an umbrella!";
    }


    @Override
    public Specializations getSpec() {
        return Specializations.AQUAMANCER;
    }
}
