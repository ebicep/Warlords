package com.ebicep.warlords.pve.items.types.specialitems.buckler.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

public class FrostyShield extends SpecialGammaBuckler implements DamageReductionandAggroPrio, CraftsInto.CraftsBucklerPiece {

    @Override
    public String getName() {
        return "Frosty Shield";
    }

    @Override
    public String getBonus() {
        return "+5% Damage Reduction but -5 Aggro Priority.";
    }

    @Override
    public String getDescription() {
        return "Frostbitten fingers never felt so good!";
    }

    @Override
    public Classes getClasses() {
        return Classes.MAGE;
    }

}