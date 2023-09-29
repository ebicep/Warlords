package com.ebicep.warlords.pve.items.types.specialitems.tome.gamma;

import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

import java.util.Set;

public class StaticScroll extends SpecialGammaTome implements CraftsInto.CraftsPansTome {

    public StaticScroll() {

    }

    public StaticScroll(Set<BasicStatPool> basicStatPools) {
        super(basicStatPools);
    }

    @Override
    public String getName() {
        return "Static Scroll";
    }

    @Override
    public String getBonus() {
        return "+5% Cooldown Reduction but -20% Damage.";
    }

    @Override
    public String getDescription() {
        return "Unlock the mysteries of the sky...";
    }


    @Override
    public Specializations getSpec() {
        return Specializations.THUNDERLORD;
    }
}