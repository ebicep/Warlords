package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.omega;

import com.ebicep.warlords.pve.items.statpool.BasicStatPool;

import java.util.Set;

public class HandsOfTheHolyCorpse extends SpecialOmegaGauntlet {
    public HandsOfTheHolyCorpse(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    public HandsOfTheHolyCorpse() {

    }

    @Override
    public String getName() {
        return "Hands of the Holy Corpse";
    }

    @Override
    public String getBonus() {
        return "Negates the weight of your next heaviest Item.";
    }

    @Override
    public String getDescription() {
        return "Let there be light.";
    }

}
