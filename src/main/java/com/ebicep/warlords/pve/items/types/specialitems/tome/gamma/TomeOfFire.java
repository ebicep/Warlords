package com.ebicep.warlords.pve.items.types.specialitems.tome.gamma;

import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

import java.util.Set;

public class TomeOfFire extends SpecialGammaTome implements CraftsInto.CraftsFirewaterAlmanac {

    public TomeOfFire() {

    }

    public TomeOfFire(Set<BasicStatPool> basicStatPools) {
        super(basicStatPools);
    }

    @Override
    public String getName() {
        return "Tome of Fire";
    }

    @Override
    public String getBonus() {
        return "+5% Cooldown Reduction but -20% Damage.";
    }

    @Override
    public String getDescription() {
        return "Step 1: Keep the heat low!";
    }


    @Override
    public Specializations getSpec() {
        return Specializations.PYROMANCER;
    }
}