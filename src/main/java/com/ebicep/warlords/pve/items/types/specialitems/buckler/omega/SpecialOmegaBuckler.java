package com.ebicep.warlords.pve.items.types.specialitems.buckler.omega;

import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.buckler.SpecialBuckler;

import java.util.Set;

public abstract class SpecialOmegaBuckler extends SpecialBuckler {

    public SpecialOmegaBuckler(Set<BasicStatPool> statPool) {
        super(ItemTier.OMEGA, statPool);
    }

}
