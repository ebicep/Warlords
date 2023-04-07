package com.ebicep.warlords.pve.items.types.specialitems.tome;

import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.addons.ItemAddonClassBonus;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AbstractSpecialItem;
import com.ebicep.warlords.pve.items.types.ItemType;

import java.util.Set;

public abstract class SpecialTome extends AbstractSpecialItem implements ItemAddonClassBonus {

    public SpecialTome() {
    }

    public SpecialTome(ItemTier tier) {
        super(ItemType.TOME, tier);
    }

    public SpecialTome(ItemTier tier, Set<BasicStatPool> statPool) {
        super(ItemType.TOME, tier, statPool);
    }

}
