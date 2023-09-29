package com.ebicep.warlords.pve.items.types.specialitems.buckler.gamma;

import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

import java.util.Set;

public class LucidBuckler extends SpecialGammaBuckler implements DamageReductionandAggroPrio, CraftsInto.CraftsShieldOfSnatching {

    public LucidBuckler() {
    }

    public LucidBuckler(Set<BasicStatPool> basicStatPools) {
        super(basicStatPools);
    }

    @Override
    public String getName() {
        return "Lucid Buckler";
    }

    @Override
    public String getBonus() {
        return "+5% Damage Reduction but -5 Aggro Priority.";
    }

    @Override
    public String getDescription() {
        return "Some might even say its obviously there.";
    }


}