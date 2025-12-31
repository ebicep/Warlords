package com.ebicep.warlords.pve.newitems.tiers;

import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.newitems.attributes.NewItemAttribute;
import com.ebicep.warlords.util.java.NamedEnum;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;

import java.util.Map;

public enum NewItemTier implements ItemTier, NamedEnum {

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
    public Material getTerracotaMaterial() {
        return itemTier.getTerracotaMaterial();
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

    public NewItemTier next() {
        return VALUES[this.ordinal() + 1 % VALUES.length];
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
    public Map<NewItemAttribute, Pair<Float, Float>> getBonusAttributeRanges() {
        return itemTier.getBonusAttributeRanges();
    }

    @Override
    public void setBonusAttributeRanges(Map<NewItemAttribute, Pair<Float, Float>> bonusAttributeRanges) {
        itemTier.setBonusAttributeRanges(bonusAttributeRanges);
    }

    @Override
    public String getConfigFieldName() {
        return itemTier.getConfigFieldName();
    }

    public ItemTier getType() {
        return itemTier;
    }

    @Override
    public Map<Integer, Map<Spendable, Long>> rerollCost() {
        return itemTier.rerollCost();
    }

    @Override
    public void setRerollCost(Map<Integer, Map<Spendable, Long>> rerollCost) {
        itemTier.setRerollCost(rerollCost);
    }

    @Override
    public Map<Integer, Map<Spendable, Long>> lockScrollRerollCost() {
        return itemTier.lockScrollRerollCost();
    }

    @Override
    public void setLockScrollRerollCost(Map<Integer, Map<Spendable, Long>> lockScrollRerollCost) {
        itemTier.setLockScrollRerollCost(lockScrollRerollCost);
    }

}