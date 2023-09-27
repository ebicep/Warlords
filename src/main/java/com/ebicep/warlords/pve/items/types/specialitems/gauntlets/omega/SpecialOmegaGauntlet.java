package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.omega;

import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.gauntlets.SpecialGauntlet;

import java.util.Set;

public abstract class SpecialOmegaGauntlet extends SpecialGauntlet {

    public SpecialOmegaGauntlet() {
        this.tier = ItemTier.OMEGA;
    }

    public SpecialOmegaGauntlet(Set<BasicStatPool> statPool) {
        super(ItemTier.OMEGA, statPool);
    }

}
