package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.omega;

import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.statpool.SpecialStatPool;
import com.ebicep.warlords.pve.items.statpool.StatPool;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GlassKnuckles extends SpecialOmegaGauntlet {

    private static final HashMap<StatPool, Integer> BONUS_STATS = new HashMap<>() {{
        put(SpecialStatPool.DAMAGE_RESISTANCE, -20);
        put(BasicStatPool.DAMAGE, 100);
    }};

    public GlassKnuckles() {

    }

    public GlassKnuckles(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    @Override
    public String getName() {
        return "Glass Knuckles";
    }

    @Override
    public String getBonus() {
        return "-20% Damage Reduction, but deal 10% more damage.";
    }

    @Override
    public String getDescription() {
        return "Ouch.";
    }

    @Override
    public Map<StatPool, Integer> getBonusStats() {
        return BONUS_STATS;
    }

}
