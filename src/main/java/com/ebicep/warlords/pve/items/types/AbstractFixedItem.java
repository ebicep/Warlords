package com.ebicep.warlords.pve.items.types;

import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import org.bukkit.ChatColor;

import java.util.HashMap;

public abstract class AbstractFixedItem extends AbstractItem {

    public AbstractFixedItem() {
    }

    public AbstractFixedItem(ItemType type, ItemTier tier) {
        this.type = type;
        this.tier = tier;
    }

    @Override
    public ItemBuilder generateItemBuilder() {
        ItemBuilder itemBuilder = getBaseItemBuilder();
        addStatPoolAndBlessing(itemBuilder);
        if (this instanceof AppliesToPlayer) {
            AppliesToPlayer bonus = (AppliesToPlayer) this;
            itemBuilder.addLore(
                    "",
                    ChatColor.GREEN + bonus.getEffect(),
                    WordWrap.wrapWithNewline(ChatColor.GRAY + bonus.getEffectDescription(), 160)
            );
        }
        addItemScoreAndWeight(itemBuilder);
        return itemBuilder;
    }

    @Override
    public String getItemName() {
        return ChatColor.GRAY + getName();
    }

    public abstract String getName();

    @Override
    protected String getItemScoreString() {
        return null;
    }

    @Override
    public abstract HashMap<BasicStatPool, Integer> getStatPool();

    @Override
    public float getItemScore() {
        return 0;
    }

    @Override
    public abstract int getWeight();
}
