package com.ebicep.warlords.pve.bountysystem.costs;

import com.ebicep.warlords.pve.Currencies;

import java.util.LinkedHashMap;

public interface BountyCost {

    LinkedHashMap<Currencies, Long> getCost();

}
