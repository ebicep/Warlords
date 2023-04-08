package com.ebicep.warlords.pve.items.types.specialitems.buckler.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

public class BloodyChakram extends SpecialGammaBuckler implements DamageReductionandKBRes, CraftsInto.CraftsPridwensBulwark {

    @Override
    public String getName() {
        return "Bloody Chakram";
    }

    @Override
    public String getBonus() {
        return "+5% Damage Reduction but -20% Knockback Resistance.";
    }

    @Override
    public String getDescription() {
        return "Definitely NOT painted red.";
    }

    @Override
    public Classes getClasses() {
        return Classes.WARRIOR;
    }

}