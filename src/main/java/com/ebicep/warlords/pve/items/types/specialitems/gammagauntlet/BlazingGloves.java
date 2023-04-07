package com.ebicep.warlords.pve.items.types.specialitems.gammagauntlet;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.statpool.StatPool;

import java.util.HashMap;

public class BlazingGloves extends SpecialGammaGauntlet implements EPSandMaxEnergy {

    public BlazingGloves(ItemTier tier) {
        super(tier);
    }

    @Override
    public HashMap<StatPool, Integer> getBonusStats() {
        return EPSandMaxEnergy.super.getBonusStats();
    }

    @Override
    public String getName() {
        return "Blazing Gloves";
    }

    @Override
    public String getBonus() {
        return "Increase energy gain by 3 per second but reduces energy cap by 20%";
    }

    @Override
    public String getDescription() {
        return "It seems you have spontaneously combusted.";
    }

    @Override
    public Classes getClasses() {
        return Classes.MAGE;
    }

}
