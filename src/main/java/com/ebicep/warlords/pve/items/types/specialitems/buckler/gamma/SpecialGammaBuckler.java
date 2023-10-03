package com.ebicep.warlords.pve.items.types.specialitems.buckler.gamma;

import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.addons.ItemAddonSpecBonus;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;
import com.ebicep.warlords.pve.items.types.specialitems.buckler.SpecialBuckler;

import java.util.Set;

public abstract class SpecialGammaBuckler extends SpecialBuckler implements ItemAddonSpecBonus, CraftsInto {

    public SpecialGammaBuckler() {
    }

    public SpecialGammaBuckler(Set<BasicStatPool> statPool) {
        super(ItemTier.GAMMA, statPool);
    }

}
