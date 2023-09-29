package com.ebicep.warlords.pve.items.types.specialitems.tome.gamma;

import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;
import com.ebicep.warlords.pve.items.types.specialitems.tome.SpecialTome;

import java.util.Set;

public abstract class SpecialGammaTome extends SpecialTome implements CraftsInto {

    public SpecialGammaTome() {
    }

    public SpecialGammaTome(Set<BasicStatPool> statPool) {
        super(ItemTier.GAMMA, statPool);
    }

}
