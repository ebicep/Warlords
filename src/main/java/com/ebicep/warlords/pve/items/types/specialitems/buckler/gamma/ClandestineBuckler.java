package com.ebicep.warlords.pve.items.types.specialitems.buckler.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.statpool.StatPool;

import java.util.HashMap;

public class ClandestineBuckler extends SpecialGammaBuckler implements DamageReductionandKBRes {

    @Override
    public String getName() {
        return "Clandestine Buckler";
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
        return "Some might even say it's not even there.";
    }

    @Override
    public Classes getClasses() {
        return Classes.ROGUE;
    }

}