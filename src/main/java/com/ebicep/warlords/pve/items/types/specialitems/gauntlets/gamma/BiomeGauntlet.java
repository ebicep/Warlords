package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.gamma;

import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

import java.util.Set;

public class BiomeGauntlet extends SpecialGammaGauntlet implements CraftsInto.CraftsGardeningGloves {

    public BiomeGauntlet() {
    }

    public BiomeGauntlet(Set<BasicStatPool> basicStatPools) {
        super(basicStatPools);
    }


    @Override
    public String getName() {
        return "Biome Gauntlet";
    }

    @Override
    public String getBonus() {
        return "+5 EPS but -20% EPH";
    }

    @Override
    public String getDescription() {
        return "One touch and you're underground.";
    }

    @Override
    public Specializations getSpec() {
        return Specializations.EARTHWARDEN;
    }
}
