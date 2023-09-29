package com.ebicep.warlords.pve.items.types.specialitems.tome.gamma;

import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

import java.util.Set;

public class NecroticScroll extends SpecialGammaTome implements CraftsInto.CraftsPansTome {

    public NecroticScroll() {

    }

    public NecroticScroll(Set<BasicStatPool> basicStatPools) {
        super(basicStatPools);
    }

    @Override
    public String getName() {
        return "Necrotic Scroll";
    }

    @Override
    public String getBonus() {
        return "+5% Cooldown Reduction but -20% Crit Chance.";
    }

    @Override
    public String getDescription() {
        return "Unlock the mysteries of the dead...";
    }


    @Override
    public Specializations getSpec() {
        return Specializations.SPIRITGUARD;
    }
}
