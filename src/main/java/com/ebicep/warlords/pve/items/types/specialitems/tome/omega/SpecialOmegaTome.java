package com.ebicep.warlords.pve.items.types.specialitems.tome.omega;

import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.gauntlets.SpecialGauntlet;

import java.util.Set;

public abstract class SpecialOmegaTome extends SpecialGauntlet {

    public SpecialOmegaTome() {
        super(ItemTier.OMEGA);
    }

    public SpecialOmegaTome(Set<BasicStatPool> statPool) {
        super(ItemTier.OMEGA, statPool);
    }

}
