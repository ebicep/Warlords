
package com.ebicep.warlords.pve.items.types.specialitems.tome.gamma;

import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

import java.util.Set;

public class TranslucentGrimiore extends SpecialGammaTome implements CraftsInto.CraftsBruisedBook {

    public TranslucentGrimiore() {

    }

    public TranslucentGrimiore(Set<BasicStatPool> basicStatPools) {
        super(basicStatPools);
    }

    @Override
    public String getName() {
        return "Translucent Grimiore";
    }

    @Override
    public String getBonus() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Guide me, master of my ship.";
    }


    @Override
    public Specializations getSpec() {
        return Specializations.LUMINARY;
    }
}