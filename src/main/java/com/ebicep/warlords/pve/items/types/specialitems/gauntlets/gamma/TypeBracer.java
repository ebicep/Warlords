package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.gamma;

import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

import java.util.Set;

public class TypeBracer extends SpecialGammaGauntlet implements CraftsInto.CraftsDiabolicalRings {

    public TypeBracer() {
    }

    public TypeBracer(Set<BasicStatPool> basicStatPools) {
        super(basicStatPools);
    }


    @Override
    public String getName() {
        return "100-Type Bracer";
    }


    @Override
    public String getDescription() {
        return "A prayer comes from the heart, fool!";
    }

    @Override
    public String getBonus() {
        return "";
    }

    @Override
    public Specializations getSpec() {
        return Specializations.LUMINARY;
    }
}
