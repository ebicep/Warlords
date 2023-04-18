package com.ebicep.warlords.pve.items.types.specialitems.tome.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

public class GuideToJuJitsu extends SpecialGammaTome implements CDRandDamage, CraftsInto.CraftsAGuideToMMA {

    @Override
    public String getName() {
        return "Guide to Ju-Jitsu";
    }

    @Override
    public String getBonus() {
        return "+5% Cooldown Reduction but -20% Damage.";
    }

    @Override
    public String getDescription() {
        return "You are your own undoing.";
    }

    @Override
    public Classes getClasses() {
        return Classes.WARRIOR;
    }

}