package com.ebicep.warlords.pve.items.types;

import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.modifiers.ItemBucklerModifier;
import com.ebicep.warlords.pve.items.statpool.ItemBucklerStatPool;

import java.util.HashMap;

public class ItemBuckler extends AbstractItem<ItemBucklerStatPool, ItemBucklerModifier.Blessings, ItemBucklerModifier.Curses> {

    public ItemBuckler(ItemTier tier) {
        super(tier, tier.generateStatPool(ItemBucklerStatPool.VALUES));
    }

    @Override
    public HashMap<ItemBucklerStatPool, ItemTier.StatRange> getTierStatRanges() {
        return tier.bucklerStatRange;
    }

    @Override
    public ItemTypes getType() {
        return ItemTypes.BUCKLER;
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
