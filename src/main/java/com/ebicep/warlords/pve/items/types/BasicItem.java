package com.ebicep.warlords.pve.items.types;

import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;

import java.util.Set;

public class BasicItem extends AbstractItem {

    public BasicItem() {
    }

    public BasicItem(ItemType type, ItemTier tier) {
        super(type, tier);
    }

    public BasicItem(ItemType type, ItemTier tier, Set<BasicStatPool> statPool) {
        super(type, tier, statPool);
    }

}
