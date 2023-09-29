package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.gamma;

import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

import java.util.Set;

public class ArchangelsFist extends SpecialGammaGauntlet implements CraftsInto.CraftsSamsonsFists {

    public ArchangelsFist() {
    }

    public ArchangelsFist(Set<BasicStatPool> basicStatPools) {
        super(basicStatPools);
    }


    @Override
    public String getName() {
        return "Archangel's Fist";
    }

    @Override
    public String getBonus() {
        return "Increase energy gain by 3 per second but reduces energy cap by 20%.";
    }

    @Override
    public String getDescription() {
        return "Behold! Judgement from on high!";
    }

    @Override
    public Specializations getSpec() {
        return Specializations.AVENGER;
    }
}
