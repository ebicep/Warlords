package com.ebicep.warlords.pve.items.types;

import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.modifiers.ItemGauntletModifier;
import com.ebicep.warlords.pve.items.statpool.ItemGauntletStatPool;

import java.util.HashMap;

public class ItemGauntlet extends AbstractItem<ItemGauntletStatPool, ItemGauntletModifier.Blessings, ItemGauntletModifier.Curses> {

    public ItemGauntlet() {
    }

    public ItemGauntlet(ItemTier tier) {
        super(tier, tier.generateStatPool(ItemGauntletStatPool.VALUES));
    }

    @Override
    public ItemGauntlet clone() {
        ItemGauntlet itemBuckler = new ItemGauntlet();
        itemBuckler.copyFrom(this);
        return itemBuckler;
    }

    @Override
    public Class<ItemGauntletStatPool> getStatPoolClass() {
        return ItemGauntletStatPool.class;
    }

    @Override
    public HashMap<ItemGauntletStatPool, ItemTier.StatRange> getTierStatRanges() {
        return tier.gauntletStatRange;
    }

    @Override
    public ItemType getType() {
        return ItemType.GAUNTLET;
    }

    @Override
    public ItemGauntletModifier.Blessings[] getBlessings() {
        return ItemGauntletModifier.Blessings.VALUES;
    }

    @Override
    public ItemGauntletModifier.Curses[] getCurses() {
        return ItemGauntletModifier.Curses.VALUES;
    }

}
