package com.ebicep.warlords.pve.items.types.specialitems.tome.gamma;

import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

import java.util.Set;

public class TerraScroll extends SpecialGammaTome implements CraftsInto.CraftsPansTome {

    public TerraScroll() {

    }

    public TerraScroll(Set<BasicStatPool> basicStatPools) {
        super(basicStatPools);
    }

    @Override
    public String getName() {
        return "Terra Scroll";
    }

    @Override
    public String getBonus() {
        return "+5% Cooldown Reduction but -20% Healing.";
    }

    @Override
    public String getDescription() {
        return "Unlock the mysteries of the ground...";
    }


    @Override
    public Specializations getSpec() {
        return Specializations.EARTHWARDEN;
    }
}
