package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.gamma;

import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

import java.util.Set;

public class OcularBracer extends SpecialGammaGauntlet implements CraftsInto.CraftsDiabolicalRings {

    public OcularBracer() {
    }

    public OcularBracer(Set<BasicStatPool> basicStatPools) {
        super(basicStatPools);
    }


    @Override
    public String getName() {
        return "Ocular Bracer";
    }


    @Override
    public String getDescription() {
        return "Nice arm, Danzo.";
    }

    @Override
    public String getBonus() {
        return "";
    }

    @Override
    public Specializations getSpec() {
        return Specializations.SENTINEL;
    }
}
