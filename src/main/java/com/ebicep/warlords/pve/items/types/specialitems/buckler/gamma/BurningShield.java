package com.ebicep.warlords.pve.items.types.specialitems.buckler.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.statpool.StatPool;

import java.util.HashMap;

public class BurningShield extends SpecialGammaBuckler implements DamageReductionandKBRes {

    @Override
    public String getName() {
        return "Burning Shield";
    }

    @Override
    public HashMap<StatPool, Integer> getBonusStats() {
        return DamageReductionandKBRes.super.getBonusStats();
    }

    @Override
    public String getBonus() {
        return "+5% Damage Reduction but -20% KB-Resistance.";
    }

    @Override
    public String getDescription() {
        return "Overcooking limbs never smelt so good!";
    }

    @Override
    public Classes getClasses() {
        return Classes.MAGE;
    }
}