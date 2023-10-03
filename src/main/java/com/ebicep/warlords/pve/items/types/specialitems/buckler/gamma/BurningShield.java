package com.ebicep.warlords.pve.items.types.specialitems.buckler.gamma;

import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

import java.util.Set;

public class BurningShield extends SpecialGammaBuckler implements CraftsInto.CraftsBucklerPiece {

    public BurningShield() {
    }

    public BurningShield(Set<BasicStatPool> basicStatPools) {
        super(basicStatPools);
    }

    @Override
    public String getName() {
        return "Burning Shield";
    }

    @Override
    public String getBonus() {
        return "+5% Damage Reduction but -20% KB-Resistance.";
    }

    @Override
    public String getDescription() {
        return "Overcooking limbs never smelt so good!";
    }

    @Override
    public Specializations getSpec() {
        return Specializations.PYROMANCER;
    }
}