package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.statpool.StatPool;

import java.util.HashMap;

public class ToxicKnuckles extends SpecialGammaGauntlet implements EPSandMaxEnergy {

    @Override
    public HashMap<StatPool, Integer> getBonusStats() {
        return EPSandMaxEnergy.super.getBonusStats();
    }

    public ToxicKnuckles(ItemTier tier) {
        super(tier);
    }

    @Override
    public Classes getClasses() {
        return Classes.ROGUE;
    }

    @Override
    public String getName() {
        return "Toxic Knuckles";
    }

    @Override
    public String getBonus() {
        return "+5 EPS but -20% Max NRG.";
    }

    @Override
    public String getDescription() {
        return "Bites like a snake.";
    }

}
