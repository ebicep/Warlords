package com.ebicep.warlords.pve.items.types.specialitems.tome.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.statpool.StatPool;

import java.util.HashMap;

public class GuideToTaiChi extends SpecialGammaTome implements CDRandCritChance {

    @Override
    public String getName() {
        return "Guide to Tai Chi";
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
        return "Center yourself.";
    }

    @Override
    public Classes getClasses() {
        return Classes.WARRIOR;
    }

}
