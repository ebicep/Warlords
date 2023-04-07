package com.ebicep.warlords.pve.items.types.specialitems.buckler.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.statpool.StatPool;

import java.util.HashMap;

public class ThornyChakram extends SpecialGammaBuckler implements DamageReductionandRegenTimer {

    @Override
    public String getName() {
        return "Thorny Chakram";
    }

    @Override
    public HashMap<StatPool, Integer> getBonusStats() {
        return DamageReductionandRegenTimer.super.getBonusStats();
    }

    @Override
    public String getBonus() {
        return "+5% Damage Reduction but -20% Regen Timer.";
    }

    @Override
    public String getDescription() {
        return "Definitely NOT a rip-off.";
    }

    @Override
    public Classes getClasses() {
        return Classes.WARRIOR;
    }

}