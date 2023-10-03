
package com.ebicep.warlords.pve.items.types.specialitems.tome.gamma;

import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

import java.util.Set;

public class LeafGrimoire extends SpecialGammaTome implements CraftsInto.CraftsBruisedBook {

    public LeafGrimoire() {

    }

    public LeafGrimoire(Set<BasicStatPool> basicStatPools) {
        super(basicStatPools);
    }

    @Override
    public String getName() {
        return "4-Leaf Grimiore";
    }

    @Override
    public String getBonus() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Dumb and Lucky";
    }


    @Override
    public Specializations getSpec() {
        return Specializations.SENTINEL;
    }
}