package com.ebicep.warlords.pve.items.types.specialitems.tome.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.statpool.StatPool;

import java.util.HashMap;

public class PhysiciansAlmanac extends SpecialGammaTome implements CDRandHealing {

    @Override
    public String getName() {
        return "Physician's Almanac";
    }

    @Override
    public HashMap<StatPool, Integer> getBonusStats() {
        return CDRandHealing.super.getBonusStats();
    }

    @Override
    public String getBonus() {
        return "+5% Cooldown Reduction but -20% Healing.";
    }

    @Override
    public String getDescription() {
        return "200+ bones and I still don't know whats wrong with you!";
    }

    @Override
    public Classes getClasses() {
        return Classes.ROGUE;
    }


}
