package com.ebicep.warlords.pve.newitems.attributes;

import com.ebicep.warlords.database.repositories.config.ConfigBased;
import com.ebicep.warlords.util.java.Pair;

import java.util.EnumMap;
import java.util.Map;

public interface NewItemBonusAttributeRanges {

    default void init(ConfigBased configBased) {
        Map<NewItemAttribute, Pair<Float, Float>> bonusAttributeRanges = new EnumMap<>(NewItemAttribute.class);
        for (NewItemAttribute bonusAttribute : NewItemAttribute.BONUS_ATTRIBUTES) {
            bonusAttributeRanges.put(bonusAttribute, new Pair<>(
                            configBased.getValue("bonusAttributeRanges." + bonusAttribute.getDatabaseName() + ".min", float.class, true),
                            configBased.getValue("bonusAttributeRanges." + bonusAttribute.getDatabaseName() + ".max", float.class, true
                            )
                    )
            );
        }
        setBonusAttributeRanges(bonusAttributeRanges);
    }

    Map<NewItemAttribute, Pair<Float, Float>> getBonusAttributeRanges();

    void setBonusAttributeRanges(Map<NewItemAttribute, Pair<Float, Float>> bonusAttributeRanges);

}
