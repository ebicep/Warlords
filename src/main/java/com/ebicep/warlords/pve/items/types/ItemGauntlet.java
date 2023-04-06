package com.ebicep.warlords.pve.items.types;

import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.modifiers.ItemGauntletModifier;

public class ItemGauntlet extends AbstractItem<ItemGauntletModifier.Blessings, ItemGauntletModifier.Curses> {

    public ItemGauntlet() {
    }

    public ItemGauntlet(ItemTier tier) {
        super(tier);
    }

    @Override
    public ItemGauntlet clone() {
        ItemGauntlet itemBuckler = new ItemGauntlet();
        itemBuckler.copyFrom(this);
        return itemBuckler;
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
