package com.ebicep.warlords.pve.items.types;

import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.modifiers.ItemTomeModifier;
import com.ebicep.warlords.pve.items.statpool.ItemTomeStatPool;

import java.util.HashMap;
import java.util.UUID;

public class ItemTome extends AbstractItem<ItemTomeStatPool, ItemTomeModifier.Blessings, ItemTomeModifier.Curses> {

    public ItemTome(UUID uuid, ItemTier tier) {
        super(uuid, tier, tier.generateStatPool(ItemTomeStatPool.VALUES));
    }

    @Override
    public HashMap<ItemTomeStatPool, ItemTier.StatRange> getTierStatRanges() {
        return tier.tomeStatRange;
    }

    @Override
    public ItemTomeModifier.Blessings[] getBlessings() {
        return ItemTomeModifier.Blessings.VALUES;
    }

    @Override
    public ItemTomeModifier.Curses[] getCurses() {
        return ItemTomeModifier.Curses.VALUES;
    }

}
