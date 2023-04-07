package com.ebicep.warlords.pve.items.types.specialitems.tome.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.statpool.StatPool;

import java.util.HashMap;

public class TomeOfIce extends SpecialGammaTome implements CDRandCritChance {

    @Override
    public String getName() {
        return "Tome of Ice";
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
        return "Step 1: Wear layers!";
    }

    @Override
    public Classes getClasses() {
        return Classes.MAGE;
    }

}