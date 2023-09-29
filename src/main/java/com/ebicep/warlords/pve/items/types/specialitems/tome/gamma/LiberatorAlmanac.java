package com.ebicep.warlords.pve.items.types.specialitems.tome.gamma;

import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

import java.util.Set;

public class LiberatorAlmanac extends SpecialGammaTome implements CraftsInto.CraftsScrollOfUncertainty {

    public LiberatorAlmanac() {

    }

    public LiberatorAlmanac(Set<BasicStatPool> basicStatPools) {
        super(basicStatPools);
    }

    @Override
    public String getName() {
        return "Liberator's Almanac";
    }

    @Override
    public String getBonus() {
        return "+5% Cooldown Reduction but -20% Crit Chance.";
    }

    @Override
    public String getDescription() {
        return "Section 372, Clause 18J states...";
    }


    @Override
    public Specializations getSpec() {
        return Specializations.VINDICATOR;
    }
}