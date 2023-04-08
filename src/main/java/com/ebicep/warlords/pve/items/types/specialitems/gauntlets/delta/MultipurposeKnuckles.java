package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.delta;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.statpool.SpecialStatPool;
import com.ebicep.warlords.pve.items.statpool.StatPool;

import java.util.HashMap;

public class MultipurposeKnuckles extends SpecialDeltaGauntlet {

    private static final HashMap<StatPool, Integer> BONUS_STATS = new HashMap<>() {{
        put(BasicStatPool.DAMAGE, 50);
        put(SpecialStatPool.DAMAGE_RESISTANCE, 5);
        put(BasicStatPool.HEALING, 50);
        put(SpecialStatPool.EPS, 5);
    }};

    @Override
    public String getName() {
        return "Multipurpose Knuckles";
    }

    @Override
    public String getBonus() {
        return "+5% Damage, Damage Reduction, Healing, and EPS.";
    }

    @Override
    public String getDescription() {
        return "Wow! So creative!";
    }

    @Override
    public Classes getClasses() {
        return Classes.ROGUE;
    }

    @Override
    public HashMap<StatPool, Integer> getBonusStats() {
        return BONUS_STATS;
    }

}
