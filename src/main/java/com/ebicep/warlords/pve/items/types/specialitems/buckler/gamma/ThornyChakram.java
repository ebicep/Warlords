package com.ebicep.warlords.pve.items.types.specialitems.buckler.gamma;

import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

import java.util.Set;

public class ThornyChakram extends SpecialGammaBuckler implements CraftsInto.CraftsPridwensBulwark {

    public ThornyChakram() {
    }

    public ThornyChakram(Set<BasicStatPool> basicStatPools) {
        super(basicStatPools);
    }

    @Override
    public String getName() {
        return "Thorny Chakram";
    }

    @Override
    public String getBonus() {
        return "+5% Damage Reduction but -20% Regen Timer.";
    }

    @Override
    public String getDescription() {
        return "Definitely NOT a rip-off.";
    }


    @Override
    public Specializations getSpec() {
        return Specializations.REVENANT;
    }
}