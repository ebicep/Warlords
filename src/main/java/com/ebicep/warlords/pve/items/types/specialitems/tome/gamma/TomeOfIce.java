package com.ebicep.warlords.pve.items.types.specialitems.tome.gamma;

import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

import java.util.Set;

public class TomeOfIce extends SpecialGammaTome implements CraftsInto.CraftsFirewaterAlmanac {

    public TomeOfIce() {

    }

    public TomeOfIce(Set<BasicStatPool> basicStatPools) {
        super(basicStatPools);
    }

    @Override
    public String getName() {
        return "Tome of Ice";
    }

    @Override
    public String getBonus() {
        return "+5% Cooldown Reduction but -20% Crit Chance.";
    }

    @Override
    public String getDescription() {
        return "Step 1: Wear layers!";
    }


    @Override
    public Specializations getSpec() {
        return Specializations.CRYOMANCER;
    }
}