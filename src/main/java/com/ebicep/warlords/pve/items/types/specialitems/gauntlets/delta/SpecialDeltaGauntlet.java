package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.delta;

import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.addons.ItemAddonClassBonus;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;
import com.ebicep.warlords.pve.items.types.specialitems.gauntlets.SpecialGauntlet;

import java.util.Set;

public abstract class SpecialDeltaGauntlet extends SpecialGauntlet implements ItemAddonClassBonus, AppliesToWarlordsPlayer {

    public SpecialDeltaGauntlet() {
        super(ItemTier.DELTA);
    }

    public SpecialDeltaGauntlet(Set<BasicStatPool> statPool) {
        super(ItemTier.DELTA, statPool);
    }

}
