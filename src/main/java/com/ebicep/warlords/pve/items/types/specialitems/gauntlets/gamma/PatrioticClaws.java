package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.gamma;

import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

import java.util.Set;

public class PatrioticClaws extends SpecialGammaGauntlet implements CraftsInto.CraftsPendragonGauntlets {

    public PatrioticClaws() {
    }

    public PatrioticClaws(Set<BasicStatPool> basicStatPools) {
        super(basicStatPools);
    }


    @Override
    public String getName() {
        return "Patriotic Claws";
    }

    @Override
    public String getBonus() {
        return "+5 EPS but -20% Speed.";
    }

    @Override
    public String getDescription() {
        return "Only someone in the depths of despair would accept such a gift.";
    }

    @Override
    public Specializations getSpec() {
        return Specializations.DEFENDER;
    }
}
