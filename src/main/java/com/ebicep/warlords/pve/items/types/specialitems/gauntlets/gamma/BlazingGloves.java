package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.gamma;

import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

import java.util.Set;

public class BlazingGloves extends SpecialGammaGauntlet implements CraftsInto.CraftsPalmOfTheSoothsayer {


    public BlazingGloves() {
    }

    public BlazingGloves(Set<BasicStatPool> statPools) {
        super(statPools);
    }

    @Override
    public String getName() {
        return "Blazing Gloves";
    }

    @Override
    public String getBonus() {
        return "Increase energy gain by 3 per second but reduces energy cap by 20%";
    }

    @Override
    public String getDescription() {
        return "It seems you have spontaneously combusted.";
    }


    @Override
    public Specializations getSpec() {
        return Specializations.PYROMANCER;
    }
}
