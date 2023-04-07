package com.ebicep.warlords.pve.items.types.specialitems.buckler.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.statpool.StatPool;

import java.util.HashMap;

public class LucidBuckler extends SpecialGammaBuckler implements DamageReductionandAgroPrio {

    @Override
    public String getName() {
        return "Lucid Buckler";
    }

    @Override
    public HashMap<StatPool, Integer> getBonusStats() {
        return DamageReductionandAgroPrio.super.getBonusStats();
    }

    @Override
    public String getBonus() {
        return "+5% Damage Reduction but -5 Aggro Priority.";
    }

    @Override
    public String getDescription() {
        return "Some might even say its obviously there.";
    }

    @Override
    public Classes getClasses() {
        return Classes.ROGUE;
    }

}