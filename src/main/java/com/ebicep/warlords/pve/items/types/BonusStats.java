package com.ebicep.warlords.pve.items.types;

import com.ebicep.warlords.pve.items.statpool.StatPool;

import java.util.Collections;
import java.util.Map;

public interface BonusStats {

    default Map<StatPool, Integer> getBonusStats() {
        return Collections.emptyMap();
    }

}
