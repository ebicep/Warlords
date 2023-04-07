package com.ebicep.warlords.pve.items.types.specialitems.tome.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.statpool.StatPool;

import java.util.HashMap;

public class StaticScroll extends SpecialGammaTome implements CDRandDamage {

    @Override
    public String getName() {
        return "Static Scroll";
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
        return "Unlock the mysteries of the sky...";
    }

    @Override
    public Classes getClasses() {
        return Classes.SHAMAN;
    }
}