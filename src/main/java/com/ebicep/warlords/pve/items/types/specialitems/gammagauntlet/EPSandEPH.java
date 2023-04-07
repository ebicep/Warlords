package com.ebicep.warlords.pve.items.types.specialitems.gammagauntlet;

import com.ebicep.warlords.pve.items.statpool.SpecialStatPool;
import com.ebicep.warlords.pve.items.statpool.StatPool;

import java.util.HashMap;

public interface EPSandEPH extends StatPool {

    HashMap<StatPool, Integer> BONUS_STATS = new HashMap<>() {{
        put(SpecialStatPool.EPS, 5);
        put(SpecialStatPool.EPH, -20);
    }};

    default HashMap<StatPool, Integer> getBonusStats() {
        return BONUS_STATS;
    }

}
