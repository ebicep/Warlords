package com.ebicep.warlords.pve.bountysystem.costs;

import com.ebicep.warlords.pve.Currencies;

import java.util.LinkedHashMap;

public interface DailyCost extends BountyCost {

    LinkedHashMap<Currencies, Long> COST = new LinkedHashMap<>() {{
        put(Currencies.COIN, 0L);
    }};


    @Override
    default LinkedHashMap<Currencies, Long> getCost() {
        return COST;
    }

}
