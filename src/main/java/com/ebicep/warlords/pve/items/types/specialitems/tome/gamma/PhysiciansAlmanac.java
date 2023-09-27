package com.ebicep.warlords.pve.items.types.specialitems.tome.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

import java.util.Set;

public class PhysiciansAlmanac extends SpecialGammaTome implements CDRandHealing, CraftsInto.CraftsScrollOfUncertainty {

    public PhysiciansAlmanac(Set<BasicStatPool> basicStatPools) {
        super(basicStatPools);
    }

    @Override
    public String getName() {
        return "Physician's Almanac";
    }

    @Override
    public String getBonus() {
        return "+5% Cooldown Reduction but -20% Healing.";
    }

    @Override
    public String getDescription() {
        return "200+ bones and I still don't know whats wrong with you!";
    }

    @Override
    public Classes getClasses() {
        return Classes.ROGUE;
    }


}
