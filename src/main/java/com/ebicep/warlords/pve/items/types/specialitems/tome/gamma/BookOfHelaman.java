package com.ebicep.warlords.pve.items.types.specialitems.tome.gamma;

import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

import java.util.Set;

public class BookOfHelaman extends SpecialGammaTome implements CraftsInto.CraftsThePresentTestament {

    public BookOfHelaman() {

    }

    public BookOfHelaman(Set<BasicStatPool> basicStatPools) {
        super(basicStatPools);
    }

    @Override
    public String getName() {
        return "Book of Helaman";
    }

    @Override
    public String getBonus() {
        return "+5% Cooldown Reduction but -20% Damage.";
    }

    @Override
    public String getDescription() {
        return "A tale of war.";
    }


    @Override
    public Specializations getSpec() {
        return Specializations.AVENGER;
    }
}