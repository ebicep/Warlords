package com.ebicep.warlords.pve.items.types.specialitems.buckler.gamma;

import com.ebicep.warlords.player.general.Classes;

public class LucidBuckler extends SpecialGammaBuckler implements DamageReductionandAgroPrio {

    @Override
    public String getName() {
        return "Lucid Buckler";
    }

    @Override
    public String getBonus() {
        return "+5% Damage Reduction but -5 Aggro Priority.";
    }

    @Override
    public String getDescription() {
        return "Some might even say its obviously there.";
    }

    @Override
    public Classes getClasses() {
        return Classes.ROGUE;
    }

}