package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.gamma;

import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;
import com.ebicep.warlords.pve.items.types.specialitems.gauntlets.SpecialGauntlet;

import java.util.Set;

public abstract class SpecialGammaGauntlet extends SpecialGauntlet implements CraftsInto {

    public SpecialGammaGauntlet() {
    }

    public SpecialGammaGauntlet(Set<BasicStatPool> statPool) {
        super(ItemTier.GAMMA, statPool);
    }

}
