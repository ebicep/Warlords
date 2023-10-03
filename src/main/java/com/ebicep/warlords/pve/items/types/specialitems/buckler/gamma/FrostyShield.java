package com.ebicep.warlords.pve.items.types.specialitems.buckler.gamma;

import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

import java.util.Set;

public class FrostyShield extends SpecialGammaBuckler implements CraftsInto.CraftsBucklerPiece {

    public FrostyShield() {
    }

    public FrostyShield(Set<BasicStatPool> basicStatPools) {
        super(basicStatPools);
    }

    @Override
    public String getName() {
        return "Frosty Shield";
    }

    @Override
    public String getBonus() {
        return "+5% Damage Reduction but -5 Aggro Priority.";
    }

    @Override
    public String getDescription() {
        return "Frostbitten fingers never felt so good!";
    }


    @Override
    public Specializations getSpec() {
        return Specializations.CRYOMANCER;
    }
}