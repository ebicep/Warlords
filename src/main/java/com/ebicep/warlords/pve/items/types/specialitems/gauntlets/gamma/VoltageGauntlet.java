package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.statpool.StatPool;

import java.util.HashMap;

public class VoltageGauntlet extends SpecialGammaGauntlet implements EPSandMaxEnergy {

    @Override
    public HashMap<StatPool, Integer> getBonusStats() {
        return EPSandMaxEnergy.super.getBonusStats();
    }

    public VoltageGauntlet(ItemTier tier) {
        super(tier);
    }

    @Override
    public Classes getClasses() {
        return Classes.SHAMAN;
    }

    @Override
    public String getName() {
        return "Voltage Gauntlet";
    }

    @Override
    public String getBonus() {
        return "+5 EPS but -20% Max NRG.";
    }

    @Override
    public String getDescription() {
        return "One touch and you're toast.";
    }

}
