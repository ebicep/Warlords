package com.ebicep.warlords.pve.items.types.specialitems.buckler.gamma;

import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.addons.ItemAddonClassBonus;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.buckler.SpecialBuckler;

import java.util.Set;

public abstract class SpecialGammaBuckler extends SpecialBuckler implements ItemAddonClassBonus {

    public SpecialGammaBuckler() {
        super(ItemTier.GAMMA);
    }

    public SpecialGammaBuckler(Set<BasicStatPool> statPool) {
        super(ItemTier.GAMMA, statPool);
    }

}
