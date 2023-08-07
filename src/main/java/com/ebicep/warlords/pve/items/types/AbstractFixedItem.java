package com.ebicep.warlords.pve.items.types;

import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.fixeditems.FixedItemAppliesToPlayer;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class AbstractFixedItem extends AbstractItem implements BonusLore {

    public AbstractFixedItem() {
    }

    public AbstractFixedItem(ItemTier tier) {
        this.tier = tier;
    }

    @Override
    public AbstractItem clone() {
        return null; // TODO if needed
    }

    @Override
    public ItemBuilder generateItemBuilder() {
        ItemBuilder itemBuilder = getBaseItemBuilder();
        addStatPoolAndBlessing(itemBuilder, null);
        if (this instanceof FixedItemAppliesToPlayer bonus) {
            itemBuilder.addLore(
                    Component.empty(),
                    Component.text(bonus.getEffect() + ":", NamedTextColor.GREEN)
            );
            itemBuilder.addLore(WordWrap.wrap(Component.text(bonus.getEffectDescription(), NamedTextColor.GRAY), 160));
        }
        addItemScoreAndWeight(itemBuilder, false);
        if (isFavorite()) {
            itemBuilder.addLore(
                    Component.empty(),
                    Component.text("FAVORITE", NamedTextColor.LIGHT_PURPLE)
            );
        }
        return itemBuilder;
    }

    @Override
    public Component getItemName() {
        return Component.text(getName(), NamedTextColor.GRAY);
    }

    public abstract String getName();

    @Override
    protected Component getItemScoreString(boolean obfuscated) {
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
    public List<Component> getBonusLore() {
        if (this instanceof FixedItemAppliesToPlayer bonus) {
            List<Component> bonusLore = new ArrayList<>();
            bonusLore.add(Component.text(bonus.getEffect() + ":", NamedTextColor.GREEN));
            bonusLore.addAll(WordWrap.wrap(Component.text(bonus.getEffectDescription(), NamedTextColor.GRAY), 160));
            return bonusLore;
        }
        return null;
    }

    @Override
    public abstract ItemType getType();
}
