package com.ebicep.warlords.pve.newitems.tiers;

import com.ebicep.warlords.pve.newitems.attributes.NewItemAttribute;
import com.ebicep.warlords.util.java.Pair;

import java.util.EnumMap;
import java.util.Map;

public abstract class BaseTier implements ItemTier {

    private String name;
    private int bonusAttributes;
    private Map<NewItemAttribute, Pair<Short, Short>> bonusAttributeRanges;

    @Override
    public void init() {
        this.name = getValue("name", String.class);
        Map<NewItemAttribute, Pair<Short, Short>> bonusAttributeRanges = new EnumMap<>(NewItemAttribute.class);
        for (NewItemAttribute bonusAttribute : NewItemAttribute.BONUS_ATTRIBUTES) {
            bonusAttributeRanges.put(bonusAttribute, new Pair<>(
                            getValue("bonusAttributeRanges." + bonusAttribute.getDatabaseName() + ".min", short.class, true),
                            getValue("bonusAttributeRanges." + bonusAttribute.getDatabaseName() + ".max", short.class, true
                            )
                    )
            );
        }
        this.bonusAttributeRanges = bonusAttributeRanges;
        this.bonusAttributes = getValue("bonusAttributes", int.class);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int bonusAttributes() {
        return bonusAttributes;
    }

    @Override
    public Map<NewItemAttribute, Pair<Short, Short>> bonusAttributeRanges() {
        return bonusAttributeRanges;
    }

}
