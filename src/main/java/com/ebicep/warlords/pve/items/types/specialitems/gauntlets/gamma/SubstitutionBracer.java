package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.gamma;

import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

import java.util.Set;

public class SubstitutionBracer extends SpecialGammaGauntlet implements CraftsInto.CraftsDiabolicalRings {

    public SubstitutionBracer() {
    }

    public SubstitutionBracer(Set<BasicStatPool> basicStatPools) {
        super(basicStatPools);
    }


    @Override
    public String getName() {
        return "Substitution Bracer";
    }


    @Override
    public String getDescription() {
        return "This feels... wrong.";
    }

    @Override
    public String getBonus() {
        return "";
    }

    @Override
    public Specializations getSpec() {
        return Specializations.CONJURER;
    }
}
