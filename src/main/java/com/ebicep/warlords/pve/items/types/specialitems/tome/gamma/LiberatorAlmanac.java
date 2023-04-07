package com.ebicep.warlords.pve.items.types.specialitems.tome.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.statpool.StatPool;

import java.util.HashMap;

public class LiberatorAlmanac extends SpecialGammaTome implements CDRandCritChance {

    @Override
    public String getName() {
        return "Liberator's Almanac";
    }

    @Override
    public HashMap<StatPool, Integer> getBonusStats() {
        return CDRandCritChance.super.getBonusStats();
    }

    @Override
    public String getBonus() {
        return "+5% Cooldown Reduction but -20% Crit Chance.";
    }

    @Override
    public String getDescription() {
        return "Section 372, Clause 18J states...";
    }

    @Override
    public Classes getClasses() {
        return Classes.ROGUE;
    }

}