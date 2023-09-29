package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.gamma;

import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

import java.util.Set;

public class ConquerorsFist extends SpecialGammaGauntlet implements CraftsInto.CraftsSamsonsFists {

    public ConquerorsFist() {
    }

    public ConquerorsFist(Set<BasicStatPool> basicStatPools) {
        super(basicStatPools);
    }


    @Override
    public String getName() {
        return "Conqueror's Fist";
    }

    @Override
    public String getBonus() {
        return "+5 EPS but -20% Speed.";
    }

    @Override
    public String getDescription() {
        return "Kneel before your ruler, Godfrey of Boullion!";
    }

    @Override
    public Specializations getSpec() {
        return Specializations.CRUSADER;
    }
}
