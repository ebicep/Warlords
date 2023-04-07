package com.ebicep.warlords.pve.items.types.specialitems.tome.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.statpool.StatPool;

import java.util.HashMap;

public class TomeOfFire extends SpecialGammaTome implements CDRandDamage {

    @Override
    public String getName() {
        return "Tome of Fire";
    }

    @Override
    public HashMap<StatPool, Integer> getBonusStats() {
        return CDRandDamage.super.getBonusStats();
    }

    @Override
    public String getBonus() {
        return "+5% Cooldown Reduction but -20% Damage.";
    }

    @Override
    public String getDescription() {
        return "Step 1: Keep the heat low!";
    }

    @Override
    public Classes getClasses() {
        return Classes.MAGE;
    }
}