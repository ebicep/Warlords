package com.ebicep.warlords.pve.items.types;

import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.fixeditems.FixedItemAppliesToPlayer;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;

import java.util.HashMap;

public abstract class AbstractFixedItem extends AbstractItem implements BonusLore {

    public AbstractFixedItem() {
    }

    public AbstractFixedItem(ItemTier tier) {
        this.tier = tier;
    }

    @Override
    public ItemBuilder generateItemBuilder() {
        ItemBuilder itemBuilder = getBaseItemBuilder();
        addStatPoolAndBlessing(itemBuilder);
        if (this instanceof FixedItemAppliesToPlayer bonus) {
            itemBuilder.addLore(
                    Component.empty(),
                    Component.text(bonus.getEffect() + ":", NamedTextColor.GREEN)
            );
            itemBuilder.addLoreC(
                    WordWrap.wrap(Component.text(bonus.getEffectDescription(), NamedTextColor.GRAY), 160)
            );
        }
        addItemScoreAndWeight(itemBuilder);
        return itemBuilder;
    }

    @Override
    public Component getItemName() {
        return Component.text(getName(), NamedTextColor.GRAY);
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

    @Override
    public String getBonusLore() {
        if (this instanceof FixedItemAppliesToPlayer bonus) {
            return ChatColor.GREEN + bonus.getEffect() + ":\n" +
                    WordWrap.wrapWithNewline(ChatColor.GRAY + bonus.getEffectDescription(), 160);
        }
        return null;
    }

    @Override
    public abstract ItemType getType();
}
