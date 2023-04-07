package com.ebicep.warlords.pve.items.types.specialitems.gauntlets;

import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.addons.ItemAddonClassBonus;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AbstractSpecialItem;
import com.ebicep.warlords.pve.items.types.ItemType;

import java.util.Set;

public abstract class SpecialGauntlet extends AbstractSpecialItem implements ItemAddonClassBonus {

    public SpecialGauntlet() {
    }

    public SpecialGauntlet(ItemTier tier) {
        super(ItemType.GAUNTLET, tier);
    }

    public SpecialGauntlet(ItemTier tier, Set<BasicStatPool> statPool) {
        super(ItemType.GAUNTLET, tier, statPool);
    }

}
