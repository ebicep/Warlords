package com.ebicep.warlords.pve.items.types;

import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.modifiers.ItemBucklerModifier;

public class ItemBuckler extends AbstractItem<ItemBucklerModifier.Blessings, ItemBucklerModifier.Curses> {

    public ItemBuckler() {
    }

    public ItemBuckler(ItemTier tier) {
        super(tier);
    }

    @Override
    public ItemBuckler clone() {
        ItemBuckler itemBuckler = new ItemBuckler();
        itemBuckler.copyFrom(this);
        return itemBuckler;
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
