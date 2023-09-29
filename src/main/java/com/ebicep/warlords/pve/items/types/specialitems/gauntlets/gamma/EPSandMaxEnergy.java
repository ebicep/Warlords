package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.gamma;

import com.ebicep.warlords.pve.items.statpool.SpecialStatPool;
import com.ebicep.warlords.pve.items.statpool.StatPool;
import com.ebicep.warlords.pve.items.types.BonusStats;

import java.util.HashMap;

@Deprecated
public interface EPSandMaxEnergy extends BonusStats {

    HashMap<StatPool, Integer> BONUS_STATS = new HashMap<>() {{
        put(SpecialStatPool.EPS, 3);
        put(SpecialStatPool.MAX_ENERGY, -20);
    }};

    @Override
    default HashMap<StatPool, Integer> getBonusStats() {
        return BONUS_STATS;
    }

}
