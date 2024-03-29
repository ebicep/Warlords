package com.ebicep.warlords.pve.items.types.specialitems.buckler.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

public class StalwartChakram extends SpecialGammaBuckler implements DamageReductionandAgroPrio, CraftsInto.CraftsPridwensBulwark {

    @Override
    public String getName() {
        return "Stalwart Chakram";
    }

    @Override
    public String getBonus() {
        return "+5% Damage Reduction but -5 Aggro Priority.";
    }

    @Override
    public String getDescription() {
        return "Definitely NOT too heavy to throw.";
    }

    @Override
    public Classes getClasses() {
        return Classes.WARRIOR;
    }

}