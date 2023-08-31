package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.omega;

import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.statpool.SpecialStatPool;
import com.ebicep.warlords.pve.items.statpool.StatPool;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GlassKnuckles extends SpecialOmegaGauntlet {

    private static final HashMap<StatPool, Integer> BONUS_STATS = new HashMap<>() {{
        put(SpecialStatPool.DAMAGE_RESISTANCE, -30);
        put(BasicStatPool.DAMAGE, 300);
    }};

    public GlassKnuckles(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    public GlassKnuckles() {

    }

    @Override
    public String getName() {
        return "Glass Knuckles";
    }

    @Override
    public String getBonus() {
        return "-30% Damage Reduction, but deal 30% more damage.";
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
