package com.ebicep.warlords.pve.items.types.specialitems.buckler.delta;

import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;
import com.ebicep.warlords.pve.items.types.specialitems.buckler.SpecialBuckler;

import java.util.Set;

public abstract class SpecialDeltaBuckler extends SpecialBuckler implements AppliesToWarlordsPlayer {

    public SpecialDeltaBuckler() {
    }

    public SpecialDeltaBuckler(Set<BasicStatPool> statPool) {
        super(ItemTier.DELTA, statPool);
    }

}
