package com.ebicep.warlords.pve.items.types.specialitems.buckler.gamma;

import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.statpool.SpecialStatPool;
import com.ebicep.warlords.pve.items.statpool.StatPool;

import java.util.HashMap;

public interface DamageReductionandRegenTimer extends StatPool {

    HashMap<StatPool, Integer> BONUS_STATS = new HashMap<>() {{
        put(SpecialStatPool.DAMAGE_RESISTANCE, 5);
        put(BasicStatPool.REGEN_TIMER, -20);
    }};

    default HashMap<StatPool, Integer> getBonusStats() {
        return BONUS_STATS;
    }

}
