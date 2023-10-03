package com.ebicep.warlords.pve.items.types.specialitems.tome.gamma;

import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

import java.util.Set;

public class GuideToJuJitsu extends SpecialGammaTome implements CraftsInto.CraftsAGuideToMMA {

    public GuideToJuJitsu() {

    }

    public GuideToJuJitsu(Set<BasicStatPool> basicStatPools) {
        super(basicStatPools);
    }

    @Override
    public String getName() {
        return "Guide to Ju-Jitsu";
    }

    @Override
    public String getBonus() {
        return "+5% Cooldown Reduction but -20% Damage.";
    }

    @Override
    public String getDescription() {
        return "You are your own undoing.";
    }


    @Override
    public Specializations getSpec() {
        return Specializations.BERSERKER;
    }
}