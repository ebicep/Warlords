package com.ebicep.warlords.pve.items.types.specialitems.tome.gamma;

import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

import java.util.Set;

public class BookOfNephi extends SpecialGammaTome implements CraftsInto.CraftsThePresentTestament {

    public BookOfNephi() {

    }

    public BookOfNephi(Set<BasicStatPool> basicStatPools) {

    }

    @Override
    public String getName() {
        return "Book of Nephi";
    }

    @Override
    public String getBonus() {
        return "+5% Cooldown Reduction but -20% Healing.";
    }

    @Override
    public String getDescription() {
        return "A tale of peace.";
    }


    @Override
    public Specializations getSpec() {
        return Specializations.PROTECTOR;
    }
}
