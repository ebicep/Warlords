package com.ebicep.warlords.pve.items.types.specialitems.buckler.gamma;

import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

import java.util.Set;

public class GalvanicBulwark extends SpecialGammaBuckler implements CraftsInto.CraftsAerialAegis {

    public GalvanicBulwark() {
    }

    public GalvanicBulwark(Set<BasicStatPool> basicStatPools) {
        super(basicStatPools);
    }

    @Override
    public String getName() {
        return "Galvanic Bulwark";
    }

    @Override
    public String getBonus() {
        return "+5% Damage Reduction but -20% Knockback Resistance.";
    }

    @Override
    public String getDescription() {
        return "Basically a battery on a stick.";
    }


    @Override
    public Specializations getSpec() {
        return Specializations.THUNDERLORD;
    }
}