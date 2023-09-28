package com.ebicep.warlords.pve.items.types.specialitems.buckler.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

import java.util.Set;

public class ClandestineBuckler extends SpecialGammaBuckler implements DamageReductionandKBRes, CraftsInto.CraftsShieldOfSnatching {

    public ClandestineBuckler() {
    }

    public ClandestineBuckler(Set<BasicStatPool> basicStatPools) {
        super(basicStatPools);
    }

    @Override
    public String getName() {
        return "Clandestine Buckler";
    }

    @Override
    public String getBonus() {
        return "+5% Damage Reduction but -20% Knockback Resistance.";
    }

    @Override
    public String getDescription() {
        return "Some might even say it's not even there.";
    }

    @Override
    public Classes getClasses() {
        return Classes.ROGUE;
    }

}