package com.ebicep.warlords.pve.items.types.specialitems.tome.omega;

import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.tome.SpecialTome;

import java.util.Set;

public abstract class SpecialOmegaTome extends SpecialTome {

    public SpecialOmegaTome() {
        this.tier = ItemTier.OMEGA;
    }

    public SpecialOmegaTome(Set<BasicStatPool> statPool) {
        super(ItemTier.OMEGA, statPool);
    }

}
