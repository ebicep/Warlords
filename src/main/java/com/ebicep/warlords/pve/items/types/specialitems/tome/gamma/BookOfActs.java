package com.ebicep.warlords.pve.items.types.specialitems.tome.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

public class BookOfActs extends SpecialGammaTome implements CDRandCritChance, CraftsInto.CraftsThePresentTestament {

    @Override
    public String getName() {
        return "Book of Acts";
    }

    @Override
    public String getBonus() {
        return "+5% Cooldown Reduction but -20% Crit Chance.";
    }

    @Override
    public String getDescription() {
        return "A tale of twelve men.";
    }

    @Override
    public Classes getClasses() {
        return Classes.PALADIN;
    }

}
