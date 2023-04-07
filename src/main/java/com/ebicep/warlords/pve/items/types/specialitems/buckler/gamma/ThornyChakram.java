package com.ebicep.warlords.pve.items.types.specialitems.buckler.gamma;

import com.ebicep.warlords.player.general.Classes;

public class ThornyChakram extends SpecialGammaBuckler implements DamageReductionandRegenTimer {

    @Override
    public String getName() {
        return "Thorny Chakram";
    }

    @Override
    public String getBonus() {
        return "+5% Damage Reduction but -20% Regen Timer.";
    }

    @Override
    public String getDescription() {
        return "Definitely NOT a rip-off.";
    }

    @Override
    public Classes getClasses() {
        return Classes.WARRIOR;
    }

}