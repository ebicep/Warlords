package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.gamma;

import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

import java.util.Set;

public class BarbaricClaws extends SpecialGammaGauntlet implements CraftsInto.CraftsPendragonGauntlets {

    public BarbaricClaws() {
    }

    public BarbaricClaws(Set<BasicStatPool> basicStatPools) {
        super(basicStatPools);
    }


    @Override
    public String getName() {
        return "Barbaric Claws";
    }

    @Override
    public String getBonus() {
        return "+5 EPS but -20% Max NRG.";
    }

    @Override
    public String getDescription() {
        return "Only a madman would wear such an instrument.";
    }

    @Override
    public Specializations getSpec() {
        return Specializations.BERSERKER;
    }
}
