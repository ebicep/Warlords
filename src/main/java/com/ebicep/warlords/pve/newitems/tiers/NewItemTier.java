package com.ebicep.warlords.pve.newitems.tiers;

import com.ebicep.warlords.pve.newitems.attributes.NewItemAttribute;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

import java.util.Map;

public enum NewItemTier implements ItemTier {

    COMMON(new Common()),
    RARE(new Rare()),
    EPIC(new Epic()),
    SOVEREIGN(new Sovereign()),
    LEGENDARY(new Legendary()),
    ASCENDANT(new Ascendant()),

    ;

    public static final NewItemTier[] VALUES = values();
    private final ItemTier itemTier;

    NewItemTier(ItemTier itemTier) {
        this.itemTier = itemTier;
    }

    @Override
    public void init() {
        itemTier.init();
    }

    @Override
    public TextColor getTextColor() {
        return itemTier.getTextColor();
    }

    @Override
    public Component getStarComponent() {
        return itemTier.getStarComponent();
    }

    @Override
    public String getName() {
        return itemTier.getName();
    }

    @Override
    public int getWeight() {
        return itemTier.getWeight();
    }

    @Override
    public int bonusAttributes() {
        return itemTier.bonusAttributes();
    }

    @Override
    public Map<NewItemAttribute, Pair<Float, Float>> bonusAttributeRanges() {
        return itemTier.bonusAttributeRanges();
    }

    @Override
    public String getConfigFieldName() {
        return itemTier.getConfigFieldName();
    }

    public ItemTier getType() {
        return itemTier;
    }
}