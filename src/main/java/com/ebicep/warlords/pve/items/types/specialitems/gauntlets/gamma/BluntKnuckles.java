package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

import java.util.Set;

public class BluntKnuckles extends SpecialGammaGauntlet implements EPSandSpeed, CraftsInto.CraftsMultipurposeKnuckles {

    public BluntKnuckles() {
    }

    public BluntKnuckles(Set<BasicStatPool> basicStatPools) {
        super(basicStatPools);
    }

    @Override
    public Classes getClasses() {
        return Classes.ROGUE;
    }

    @Override
    public String getName() {
        return "Blunt Knuckles";
    }

    @Override
    public String getBonus() {
        return "+5 EPS but -20% Speed";
    }

    @Override
    public String getDescription() {
        return "Hits like a truck.";
    }

}
