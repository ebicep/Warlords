package com.ebicep.warlords.pve.newitems.tiers;

import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.newitems.attributes.NewItemAttribute;
import com.ebicep.warlords.util.java.Pair;

import java.util.EnumMap;
import java.util.Map;

public abstract class BaseTier implements ItemTier {

    private String name;
    private int weight;
    private int bonusAttributes;
    private Map<NewItemAttribute, Pair<Float, Float>> bonusAttributeRanges;
    private Map<Spendable, Long> rerollCost;
    private Map<Spendable, Long> lockScrollRerollCost;

    @Override
    public void init() {
        this.name = getValue("name", String.class);
        this.weight = getValue("weight", int.class);
        Map<NewItemAttribute, Pair<Float, Float>> bonusAttributeRanges = new EnumMap<>(NewItemAttribute.class);
        for (NewItemAttribute bonusAttribute : NewItemAttribute.BONUS_ATTRIBUTES) {
            bonusAttributeRanges.put(bonusAttribute, new Pair<>(
                    getValue("bonusAttributeRanges." + bonusAttribute.getDatabaseName() + ".min", float.class, true),
                    getValue("bonusAttributeRanges." + bonusAttribute.getDatabaseName() + ".max", float.class, true
                            )
                    )
            );
        }
        this.bonusAttributeRanges = bonusAttributeRanges;
        this.bonusAttributes = getValue("bonusAttributes", int.class);
        init(getMapValue("rerollCost", long.class), getMapValue("lockScrollRerollCost", long.class));
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
    public Map<NewItemAttribute, Pair<Float, Float>> bonusAttributeRanges() {
        return bonusAttributeRanges;
    }

    @Override
    public Map<Spendable, Long> rerollCost() {
        return rerollCost;
    }

    @Override
    public void setRerollCost(Map<Spendable, Long> rerollCost) {
        this.rerollCost = rerollCost;
    }

    @Override
    public Map<Spendable, Long> lockScrollRerollCost() {
        return lockScrollRerollCost;
    }

    @Override
    public void setLockScrollRerollCost(Map<Spendable, Long> lockScrollRerollCost) {
        this.lockScrollRerollCost = lockScrollRerollCost;
    }

}
