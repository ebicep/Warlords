package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.delta;

import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;
import com.ebicep.warlords.pve.items.types.specialitems.gauntlets.SpecialGauntlet;

import java.util.Set;

public abstract class SpecialDeltaGauntlet extends SpecialGauntlet implements CraftsInto {

    public SpecialDeltaGauntlet() {
    }

    public SpecialDeltaGauntlet(Set<BasicStatPool> statPool) {
        super(ItemTier.DELTA, statPool);
    }

}
