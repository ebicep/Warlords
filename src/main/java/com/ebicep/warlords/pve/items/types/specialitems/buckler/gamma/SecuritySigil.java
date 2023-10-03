package com.ebicep.warlords.pve.items.types.specialitems.buckler.gamma;

import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

import java.util.Set;

public class SecuritySigil extends SpecialGammaBuckler implements CraftsInto.CraftsOtherworldlyAmulet {

    public SecuritySigil() {
    }

    public SecuritySigil(Set<BasicStatPool> basicStatPools) {
        super(basicStatPools);
    }

    @Override
    public String getName() {
        return "Security Sigil";
    }

    @Override
    public String getBonus() {
        return "";
    }

    @Override
    public String getDescription() {
        return "The enemies get a little quirky at night...";
    }


    @Override
    public Specializations getSpec() {
        return Specializations.SENTINEL;
    }
}