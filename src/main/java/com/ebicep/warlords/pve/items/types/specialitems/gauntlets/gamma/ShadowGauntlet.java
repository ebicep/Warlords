package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

import java.util.Set;

public class ShadowGauntlet extends SpecialGammaGauntlet implements EPSandSpeed, CraftsInto.CraftsGardeningGloves {

    public ShadowGauntlet() {
    }

    public ShadowGauntlet(Set<BasicStatPool> basicStatPools) {
        super(basicStatPools);
    }

    @Override
    public Classes getClasses() {
        return Classes.SHAMAN;
    }

    @Override
    public String getName() {
        return "Shadow Gauntlet";
    }

    @Override
    public String getBonus() {
        return "+5 EPS but -20% Speed";
    }

    @Override
    public String getDescription() {
        return "One touch and you're floating.";
    }

}
