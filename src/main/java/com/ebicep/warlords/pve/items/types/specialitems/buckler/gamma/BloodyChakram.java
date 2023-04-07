package com.ebicep.warlords.pve.items.types.specialitems.buckler.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.statpool.StatPool;

import java.util.HashMap;

public class BloodyChakram extends SpecialGammaBuckler implements DamageReductionandKBRes {

    @Override
    public String getName() {
        return "Bloody Chakram";
    }

    @Override
    public HashMap<StatPool, Integer> getBonusStats() {
        return DamageReductionandKBRes.super.getBonusStats();
    }

    @Override
    public String getBonus() {
        return "+5% Damage Reduction but -20% Knockback Resistance.";
    }

    @Override
    public String getDescription() {
        return "Definitely NOT painted red.";
    }

    @Override
    public Classes getClasses() {
        return Classes.WARRIOR;
    }

}