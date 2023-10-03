package com.ebicep.warlords.pve.items.types.specialitems.tome.delta;

import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;
import com.ebicep.warlords.pve.items.types.specialitems.tome.SpecialTome;

import java.util.Set;

public abstract class SpecialDeltaTome extends SpecialTome implements AppliesToWarlordsPlayer {

    public SpecialDeltaTome() {
    }

    public SpecialDeltaTome(Set<BasicStatPool> statPool) {
        super(ItemTier.DELTA, statPool);
    }

}
