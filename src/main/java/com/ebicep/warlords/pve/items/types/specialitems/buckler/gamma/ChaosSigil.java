package com.ebicep.warlords.pve.items.types.specialitems.buckler.gamma;

import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

import java.util.Set;

public class ChaosSigil extends SpecialGammaBuckler implements CraftsInto.CraftsOtherworldlyAmulet {

    public ChaosSigil() {
    }

    public ChaosSigil(Set<BasicStatPool> basicStatPools) {
        super(basicStatPools);
    }

    @Override
    public String getName() {
        return "Chaos Sigil";
    }

    @Override
    public String getBonus() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Basically nothing on a stick.";
    }


    @Override
    public Specializations getSpec() {
        return Specializations.CONJURER;
    }
}