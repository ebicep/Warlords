package com.ebicep.warlords.pve.newitems.tiers;

import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.newitems.attributes.NewItemAttribute;
import com.ebicep.warlords.util.java.Pair;

import java.util.Map;

public abstract class BaseTier implements ItemTier {

    private String name;
    private int weight;
    private int bonusAttributes;
    private Map<NewItemAttribute, Pair<Float, Float>> bonusAttributeRanges;
    private Map<Integer, Map<Spendable, Long>> rerollCost;
    private Map<Integer, Map<Spendable, Long>> lockScrollRerollCost;

    @Override
    public void init() {
        this.name = getValue("name", String.class);
        this.weight = getValue("weight", int.class);
        this.bonusAttributes = getValue("bonusAttributes", int.class);
        init(this);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getWeight() {
        return weight;
    }

    @Override
    public int bonusAttributes() {
        return bonusAttributes;
    }

    @Override
    public Map<NewItemAttribute, Pair<Float, Float>> getBonusAttributeRanges() {
        return bonusAttributeRanges;
    }

    @Override
    public void setBonusAttributeRanges(Map<NewItemAttribute, Pair<Float, Float>> bonusAttributeRanges) {
        this.bonusAttributeRanges = bonusAttributeRanges;
    }

    @Override
    public Map<Integer, Map<Spendable, Long>> getRerollCost() {
        return rerollCost;
    }

    @Override
    public void setRerollCost(Map<Integer, Map<Spendable, Long>> rerollCost) {
        this.rerollCost = rerollCost;
    }

    @Override
    public Map<Integer, Map<Spendable, Long>> getLockScrollRerollCost() {
        return lockScrollRerollCost;
    }

    @Override
    public void setLockScrollRerollCost(Map<Integer, Map<Spendable, Long>> lockScrollRerollCost) {
        this.lockScrollRerollCost = lockScrollRerollCost;
    }

}
