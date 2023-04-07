package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.statpool.StatPool;

import java.util.HashMap;

public class DrippingGloves extends SpecialGammaGauntlet implements EPSandEPH {

    @Override
    public HashMap<StatPool, Integer> getBonusStats() {
        return EPSandEPH.super.getBonusStats();
    }

    @Override
    public String getName() {
        return "Dripping Gloves";
    }

    @Override
    public String getBonus() {
        return "+5 EPS but -20% EPH";
    }

    @Override
    public String getDescription() {
        return "It seems that air drying isn't very effective.";
    }

    @Override
    public Classes getClasses() {
        return Classes.MAGE;
    }

}
