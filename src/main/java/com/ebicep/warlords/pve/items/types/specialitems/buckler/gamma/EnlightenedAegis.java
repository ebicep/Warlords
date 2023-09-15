package com.ebicep.warlords.pve.items.types.specialitems.buckler.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

public class EnlightenedAegis extends SpecialGammaBuckler implements DamageReductionandAggroPrio, CraftsInto.CraftsCrossNecklaceCharm {

    @Override
    public String getName() {
        return "Enlightened Aegis";
    }

    @Override
    public String getBonus() {
        return "+5% Damage Reduction but -5 Aggro Priority.";
    }

    @Override
    public String getDescription() {
        return "Victory or Death, right?";
    }

    @Override
    public Classes getClasses() {
        return Classes.PALADIN;
    }

}
