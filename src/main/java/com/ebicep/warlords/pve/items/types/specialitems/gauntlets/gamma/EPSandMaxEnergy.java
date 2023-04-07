package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.gamma;

import com.ebicep.warlords.pve.items.statpool.SpecialStatPool;
import com.ebicep.warlords.pve.items.statpool.StatPool;

import java.util.HashMap;

public interface EPSandMaxEnergy extends StatPool {

    HashMap<StatPool, Integer> BONUS_STATS = new HashMap<>() {{
        put(SpecialStatPool.EPS, 3);
        put(SpecialStatPool.MAX_ENERGY, -20);
    }};

    default HashMap<StatPool, Integer> getBonusStats() {
        return BONUS_STATS;
    }

}
