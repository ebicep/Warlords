package com.ebicep.warlords.pve.bountysystem.costs;

import com.ebicep.warlords.pve.Currencies;

import java.util.LinkedHashMap;

public interface EventCost extends BountyCost {

    LinkedHashMap<Currencies, Long> COST = new LinkedHashMap<>() {{
        put(Currencies.COIN, 25_000L);
    }};


    @Override
    default LinkedHashMap<Currencies, Long> getCost() {
        return COST;
    }

}
