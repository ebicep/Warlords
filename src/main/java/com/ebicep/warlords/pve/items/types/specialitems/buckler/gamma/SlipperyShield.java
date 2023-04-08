package com.ebicep.warlords.pve.items.types.specialitems.buckler.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

public class SlipperyShield extends SpecialGammaBuckler implements DamageReductionandRegenTimer, CraftsInto.CraftsBucklerPiece {

    @Override
    public String getName() {
        return "Slippery Shield";
    }

    @Override
    public String getBonus() {
        return "+5% Damage Reduction but -20% Regen Timer.";
    }

    @Override
    public String getDescription() {
        return "Slick surfaces never worked so well!";
    }

    @Override
    public Classes getClasses() {
        return Classes.MAGE;
    }

}