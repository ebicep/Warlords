package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

import java.util.Set;

public class ToxicKnuckles extends SpecialGammaGauntlet implements EPSandMaxEnergy, CraftsInto.CraftsMultipurposeKnuckles {

    public ToxicKnuckles() {
    }

    public ToxicKnuckles(Set<BasicStatPool> basicStatPools) {
        super(basicStatPools);
    }

    @Override
    public Classes getClasses() {
        return Classes.ROGUE;
    }

    @Override
    public String getName() {
        return "Toxic Knuckles";
    }

    @Override
    public String getBonus() {
        return "+5 EPS but but reduces energy cap by 20%.";
    }

    @Override
    public String getDescription() {
        return "Bites like a snake.";
    }

}
