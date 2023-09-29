package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.gamma;

import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

import java.util.Set;

public class EidolonicClaws extends SpecialGammaGauntlet implements CraftsInto.CraftsPendragonGauntlets {

    public EidolonicClaws() {
    }

    public EidolonicClaws(Set<BasicStatPool> basicStatPools) {
        super(basicStatPools);
    }


    @Override
    public String getName() {
        return "Eidolonic Claws";
    }

    @Override
    public String getBonus() {
        return "+5 EPS but -20% EPH";
    }

    @Override
    public String getDescription() {
        return "Only a man who doesn't fear death would touch such an object.";
    }


    @Override
    public Specializations getSpec() {
        return Specializations.REVENANT;
    }
}
