
package com.ebicep.warlords.pve.items.types.specialitems.tome.gamma;

import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

import java.util.Set;

public class IntangibleGrimoire extends SpecialGammaTome implements CraftsInto.CraftsBruisedBook {

    public IntangibleGrimoire() {

    }

    public IntangibleGrimoire(Set<BasicStatPool> basicStatPools) {
        super(basicStatPools);
    }

    @Override
    public String getName() {
        return "Intangible Grimiore";
    }

    @Override
    public String getBonus() {
        return "";
    }

    @Override
    public String getDescription() {
        return "...Where'd it go?";
    }


    @Override
    public Specializations getSpec() {
        return Specializations.CONJURER;
    }
}