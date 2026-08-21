package com.ebicep.warlords.pve.newitems;

import com.ebicep.warlords.database.configuration.SpendableParser;
import com.ebicep.warlords.database.repositories.config.ConfigBased;
import com.ebicep.warlords.pve.Spendable;
import org.apache.commons.collections4.map.HashedMap;

import java.util.LinkedHashMap;
import java.util.Map;

public interface NewItemRerollCost {

    int MAX_REROLLS = 3;
    int MAX_LOCKED_ATTRIBUTES = 1;

    default void init(ConfigBased configBased) {
        Map<Integer, Map<Spendable, Long>> rerollCost = new HashedMap<>();
        Map<Integer, Map<Spendable, Long>> lockScrollRerollCost = new HashedMap<>();
        for (int i = 1; i <= MAX_REROLLS; i++) {
            Map<String, Long> rerollMap = configBased.getMapValue("rerollCost." + i, long.class);
            Map<String, Long> lockScrollRerollMap = configBased.getMapValue("lockScrollRerollCost." + i, long.class);

            rerollCost.put(i, toSpendableMap(rerollMap));
            lockScrollRerollCost.put(i, toSpendableMap(lockScrollRerollMap));
        }
        setRerollCost(rerollCost);
        setLockScrollRerollCost(lockScrollRerollCost);
    }

    private static Map<Spendable, Long> toSpendableMap(Map<String, Long> map) {
        Map<Spendable, Long> spendableMap = new LinkedHashMap<>();
        for (Map.Entry<String, Long> entry : map.entrySet()) {
            try {
                spendableMap.put(SpendableParser.parse(entry.getKey()), entry.getValue());
            } catch (Exception ignored) {
            }
        }
        return spendableMap;
    }

    Map<Integer, Map<Spendable, Long>> getRerollCost();

    void setRerollCost(Map<Integer, Map<Spendable, Long>> rerollCost);

    Map<Integer, Map<Spendable, Long>> getLockScrollRerollCost();

    void setLockScrollRerollCost(Map<Integer, Map<Spendable, Long>> lockScrollRerollCost);

}
