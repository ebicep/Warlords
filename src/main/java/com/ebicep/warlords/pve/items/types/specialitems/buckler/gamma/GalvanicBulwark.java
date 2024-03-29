package com.ebicep.warlords.pve.items.types.specialitems.buckler.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

public class GalvanicBulwark extends SpecialGammaBuckler implements DamageReductionandKBRes, CraftsInto.CraftsAerialAegis {

    @Override
    public String getName() {
        return "Galvanic Bulwark";
    }

    @Override
    public String getBonus() {
        return "+5% Damage Reduction but -20% Knockback Resistance.";
    }

    @Override
    public String getDescription() {
        return "Basically a battery on a stick.";
    }

    @Override
    public Classes getClasses() {
        return Classes.SHAMAN;
    }

}