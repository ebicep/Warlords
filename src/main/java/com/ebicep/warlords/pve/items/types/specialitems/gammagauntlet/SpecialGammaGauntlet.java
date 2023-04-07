package com.ebicep.warlords.pve.items.types.specialitems.gammagauntlet;

import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.addons.ItemAddonClassBonus;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.ItemType;
import com.ebicep.warlords.pve.items.types.SpecialItem;

import java.util.Set;

public abstract class SpecialGammaGauntlet extends SpecialItem implements ItemAddonClassBonus {

    public SpecialGammaGauntlet() {
    }

    public SpecialGammaGauntlet(ItemTier tier) {
        super(ItemType.GAUNTLET, tier);
    }

    public SpecialGammaGauntlet(ItemTier tier, Set<BasicStatPool> statPool) {
        super(ItemType.GAUNTLET, tier, statPool);
    }

}
