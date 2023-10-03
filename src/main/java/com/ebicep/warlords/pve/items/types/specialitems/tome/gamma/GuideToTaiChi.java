package com.ebicep.warlords.pve.items.types.specialitems.tome.gamma;

import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

import java.util.Set;

public class GuideToTaiChi extends SpecialGammaTome implements CraftsInto.CraftsAGuideToMMA {

    public GuideToTaiChi() {

    }

    public GuideToTaiChi(Set<BasicStatPool> basicStatPools) {
        super(basicStatPools);
    }

    @Override
    public String getName() {
        return "Guide to Tai Chi";
    }

    @Override
    public String getBonus() {
        return "+5% Cooldown Reduction but -20% Crit Chance.";
    }

    @Override
    public String getDescription() {
        return "Center yourself.";
    }


    @Override
    public Specializations getSpec() {
        return Specializations.DEFENDER;
    }
}
