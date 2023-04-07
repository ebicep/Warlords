package com.ebicep.warlords.pve.items.types.specialitems.tome.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.statpool.StatPool;

import java.util.HashMap;

public class TomeOfWater extends SpecialGammaTome implements CDRandHealing {

    @Override
    public String getName() {
        return "Tome of Water";
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
        return "Step 1: Own an umbrella!";
    }

    @Override
    public Classes getClasses() {
        return Classes.MAGE;
    }

}
