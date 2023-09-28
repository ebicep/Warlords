package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

import java.util.Set;

public class PatrioticClaws extends SpecialGammaGauntlet implements EPSandSpeed, CraftsInto.CraftsPendragonGauntlets {

    public PatrioticClaws() {
    }

    public PatrioticClaws(Set<BasicStatPool> basicStatPools) {
        super(basicStatPools);
    }

    @Override
    public Classes getClasses() {
        return Classes.WARRIOR;
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

}
