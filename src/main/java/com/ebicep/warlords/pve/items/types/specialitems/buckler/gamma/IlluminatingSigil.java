package com.ebicep.warlords.pve.items.types.specialitems.buckler.gamma;

import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

import java.util.Set;

public class IlluminatingSigil extends SpecialGammaBuckler implements CraftsInto.CraftsOtherworldlyAmulet {

    public IlluminatingSigil() {
    }

    public IlluminatingSigil(Set<BasicStatPool> basicStatPools) {
        super(basicStatPools);
    }

    @Override
    public String getName() {
        return "Illuminating Sigil";
    }

    @Override
    public String getBonus() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Shhhhhhhh!!!!!";
    }


    @Override
    public Specializations getSpec() {
        return Specializations.LUMINARY;
    }
}