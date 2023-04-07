package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.statpool.StatPool;

import java.util.HashMap;

public class BluntKnuckles extends SpecialGammaGauntlet implements EPSandSpeed {

    @Override
    public HashMap<StatPool, Integer> getBonusStats() {
        return EPSandSpeed.super.getBonusStats();
    }

    public BluntKnuckles(ItemTier tier) {
        super(tier);
    }

    @Override
    public Classes getClasses() {
        return Classes.ROGUE;
    }

    @Override
    public String getName() {
        return "Blunt Knuckles";
    }

    @Override
    public String getBonus() {
        return "+5 EPS but -20% Speed";
    }

    @Override
    public String getDescription() {
        return "Hits like a truck.";
    }

}
