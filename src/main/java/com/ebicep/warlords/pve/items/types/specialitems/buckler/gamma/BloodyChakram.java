package com.ebicep.warlords.pve.items.types.specialitems.buckler.gamma;

import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

import java.util.Set;

public class BloodyChakram extends SpecialGammaBuckler implements DamageReductionandKBRes, CraftsInto.CraftsPridwensBulwark {

    public BloodyChakram() {
    }

    public BloodyChakram(Set<BasicStatPool> basicStatPools) {
        super(basicStatPools);
    }

    @Override
    public String getName() {
        return "Bloody Chakram";
    }

    @Override
    public String getBonus() {
        return "+5% Damage Reduction but -20% Knockback Resistance.";
    }

    @Override
    public String getDescription() {
        return "Definitely NOT painted red.";
    }


}