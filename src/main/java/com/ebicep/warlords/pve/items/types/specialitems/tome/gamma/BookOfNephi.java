package com.ebicep.warlords.pve.items.types.specialitems.tome.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.statpool.StatPool;

import java.util.HashMap;

public class BookOfNephi extends SpecialGammaTome implements CDRandHealing {

    @Override
    public String getName() {
        return "Book of Nephi";
    }

    @Override
    public HashMap<StatPool, Integer> getBonusStats() {
        return CDRandHealing.super.getBonusStats();
    }

    @Override
    public String getBonus() {
        return "+5% Cooldown Reduction but -20% Healing.";
    }

    @Override
    public String getDescription() {
        return "A tale of peace.";
    }

    @Override
    public Classes getClasses() {
        return Classes.PALADIN;
    }


}
