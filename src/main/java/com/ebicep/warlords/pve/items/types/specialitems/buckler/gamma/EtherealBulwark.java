package com.ebicep.warlords.pve.items.types.specialitems.buckler.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

public class EtherealBulwark extends SpecialGammaBuckler implements DamageReductionandAgroPrio, CraftsInto.CraftsAerialAegis {

    @Override
    public String getName() {
        return "Ethereal Bulwark";
    }

    @Override
    public String getBonus() {
        return "+5% Damage Reduction but -5 Aggro Priority.";
    }

    @Override
    public String getDescription() {
        return "Basically nothing on a stick.";
    }

    @Override
    public Classes getClasses() {
        return Classes.SHAMAN;
    }

}