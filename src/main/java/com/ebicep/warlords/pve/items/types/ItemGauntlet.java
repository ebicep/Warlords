package com.ebicep.warlords.pve.items.types;

import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.modifiers.ItemGauntletModifier;
import com.ebicep.warlords.pve.items.statpool.ItemGauntletStatPool;

import java.util.HashMap;
import java.util.UUID;

public class ItemGauntlet extends AbstractItem<ItemGauntletStatPool, ItemGauntletModifier.Blessings, ItemGauntletModifier.Curses> {

    public ItemGauntlet(UUID uuid, ItemTier tier) {
        super(uuid, tier, tier.generateStatPool(ItemGauntletStatPool.VALUES));
    }

    @Override
    public HashMap<ItemGauntletStatPool, ItemTier.StatRange> getTierStatRanges() {
        return tier.gauntletStatRange;
    }

}
