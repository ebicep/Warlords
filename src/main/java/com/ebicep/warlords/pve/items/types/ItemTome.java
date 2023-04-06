package com.ebicep.warlords.pve.items.types;

import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.modifiers.ItemTomeModifier;
import com.ebicep.warlords.pve.items.statpool.ItemStatPool;

import java.util.Set;

public class ItemTome extends AbstractItem<ItemTomeModifier.Blessings, ItemTomeModifier.Curses> {

    public ItemTome() {
    }

    public ItemTome(ItemTier tier) {
        super(tier);
    }

    public ItemTome(ItemTier tier, Set<ItemStatPool> statPool) {
        super(tier, statPool);
    }

    @Override
    public ItemTome clone() {
        ItemTome itemTome = new ItemTome();
        itemTome.copyFrom(this);
        return itemTome;
    }

    @Override
    public ItemType getType() {
        return ItemType.TOME;
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
