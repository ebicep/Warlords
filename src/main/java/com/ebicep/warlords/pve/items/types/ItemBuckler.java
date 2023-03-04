package com.ebicep.warlords.pve.items.types;

import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.modifiers.ItemBucklerModifier;
import com.ebicep.warlords.pve.items.statpool.ItemBucklerStatPool;

import java.util.HashMap;
import java.util.UUID;

public class ItemBuckler extends AbstractItem<ItemBucklerStatPool, ItemBucklerModifier.Blessings, ItemBucklerModifier.Curses> {

    public ItemBuckler(UUID uuid, ItemTier tier) {
        super(uuid, tier, tier.generateStatPool(ItemBucklerStatPool.VALUES));
    }

    @Override
    public HashMap<ItemBucklerStatPool, ItemTier.StatRange> getTierStatRanges() {
        return tier.bucklerStatRange;
    }

    @Override
    public ItemBucklerModifier.Blessings[] getBlessings() {
        return ItemBucklerModifier.Blessings.VALUES;
    }

    @Override
    public ItemBucklerModifier.Curses[] getCurses() {
        return ItemBucklerModifier.Curses.VALUES;
    }

}
