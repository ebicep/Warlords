package com.ebicep.warlords.pve.items.types;

import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.modifiers.ItemBucklerModifier;
import com.ebicep.warlords.pve.items.statpool.ItemBucklerStatPool;

import java.util.HashMap;

public class ItemBuckler extends AbstractItem<ItemBucklerStatPool, ItemBucklerModifier.Blessings, ItemBucklerModifier.Curses> {

    public ItemBuckler() {
    }

    public ItemBuckler(ItemTier tier) {
        super(tier, tier.generateStatPool(ItemBucklerStatPool.VALUES));
    }

    @Override
    public ItemBuckler clone() {
        ItemBuckler itemBuckler = new ItemBuckler();
        itemBuckler.copyFrom(this);
        return itemBuckler;
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

    @Override
    public ItemType getType() {
        return ItemType.BUCKLER;
    }

}
