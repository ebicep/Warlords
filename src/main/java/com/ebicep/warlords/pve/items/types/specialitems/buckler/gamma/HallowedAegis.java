package com.ebicep.warlords.pve.items.types.specialitems.buckler.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

public class HallowedAegis extends SpecialGammaBuckler implements DamageReductionandRegenTimer, CraftsInto.CraftsCrossNecklaceCharm {

    @Override
    public String getName() {
        return "Hallowed Aegis";
    }

    @Override
    public String getBonus() {
        return "+5% Damage Reduction but -20% Regen Timer.";
    }

    @Override
    public String getDescription() {
        return "Heal and Steal, right?";
    }

    @Override
    public Classes getClasses() {
        return Classes.PALADIN;
    }

}