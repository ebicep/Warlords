package com.ebicep.warlords.pve.items.types.specialitems.buckler;

import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.addons.ItemAddonClassBonus;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AbstractSpecialItem;
import com.ebicep.warlords.pve.items.types.ItemType;

import java.util.Set;

public abstract class SpecialBuckler extends AbstractSpecialItem implements ItemAddonClassBonus {

    public SpecialBuckler() {
    }

    public SpecialBuckler(ItemTier tier) {
        super(ItemType.BUCKLER, tier);
    }

    public SpecialBuckler(ItemTier tier, Set<BasicStatPool> statPool) {
        super(ItemType.BUCKLER, tier, statPool);
    }

}
