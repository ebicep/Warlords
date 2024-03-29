package com.ebicep.warlords.pve.items.types.specialitems.buckler.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

public class BurningShield extends SpecialGammaBuckler implements DamageReductionandKBRes, CraftsInto.CraftsBucklerPiece {

    @Override
    public String getName() {
        return "Burning Shield";
    }

    @Override
    public String getBonus() {
        return "+5% Damage Reduction but -20% KB-Resistance.";
    }

    @Override
    public String getDescription() {
        return "Overcooking limbs never smelt so good!";
    }

    @Override
    public Classes getClasses() {
        return Classes.MAGE;
    }
}