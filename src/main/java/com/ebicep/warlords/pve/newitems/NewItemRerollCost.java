package com.ebicep.warlords.pve.newitems;

import com.ebicep.warlords.database.configuration.StringToSpendableConverter;
import com.ebicep.warlords.pve.Spendable;

import java.util.LinkedHashMap;
import java.util.Map;

public interface NewItemRerollCost {

    default void init(Map<String, Long> rerollMap, Map<String, Long> lockScrollRerollMap) {
        StringToSpendableConverter spendableConverter = new StringToSpendableConverter();
        Map<Spendable, Long> rerollCost = new LinkedHashMap<>();
        for (Map.Entry<String, Long> entry : rerollMap.entrySet()) {
            rerollCost.put(spendableConverter.convert(entry.getKey()), entry.getValue());
        }
        setRerollCost(rerollCost);
        Map<Spendable, Long> lockScrollRerollCost = new LinkedHashMap<>();
        for (Map.Entry<String, Long> entry : lockScrollRerollMap.entrySet()) {
            lockScrollRerollCost.put(spendableConverter.convert(entry.getKey()), entry.getValue());
        }
        setLockScrollRerollCost(lockScrollRerollCost);
    }

    Map<Spendable, Long> rerollCost();

    void setRerollCost(Map<Spendable, Long> rerollCost);

    Map<Spendable, Long> lockScrollRerollCost();

    void setLockScrollRerollCost(Map<Spendable, Long> lockScrollRerollCost);

}
