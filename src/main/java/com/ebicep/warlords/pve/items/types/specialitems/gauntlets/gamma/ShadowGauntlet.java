package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.statpool.StatPool;

import java.util.HashMap;

public class ShadowGauntlet extends SpecialGammaGauntlet implements EPSandSpeed {

    @Override
    public Classes getClasses() {
        return Classes.SHAMAN;
    }

    @Override
    public String getName() {
        return "Shadow Gauntlet";
    }

    @Override
    public HashMap<StatPool, Integer> getBonusStats() {
        return EPSandSpeed.super.getBonusStats();
    }

    @Override
    public String getBonus() {
        return "+5 EPS but -20% Speed";
    }

    @Override
    public String getDescription() {
        return "One touch and you're floating.";
    }

}
