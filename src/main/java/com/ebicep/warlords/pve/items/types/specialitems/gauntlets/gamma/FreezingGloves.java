package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.statpool.StatPool;

import java.util.HashMap;

public class FreezingGloves extends SpecialGammaGauntlet implements EPSandSpeed {

    public FreezingGloves(ItemTier tier) {
        super(tier);
    }

    @Override
    public HashMap<StatPool, Integer> getBonusStats() {
        return EPSandSpeed.super.getBonusStats();
    }

    @Override
    public String getName() {
        return "Freezing Gloves";
    }

    @Override
    public String getBonus() {
        return "+5 EPS but -20% Speed.";
    }

    @Override
    public String getDescription() {
        return "It seems you have been frozen into place.";
    }

    @Override
    public Classes getClasses() {
        return Classes.MAGE;
    }


}
