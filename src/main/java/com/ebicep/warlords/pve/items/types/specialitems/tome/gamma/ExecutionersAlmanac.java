package com.ebicep.warlords.pve.items.types.specialitems.tome.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

public class ExecutionersAlmanac extends SpecialGammaTome implements CDRandDamage, CraftsInto.CraftsScrollOfUncertainty {

    @Override
    public String getName() {
        return "Executioner's Almanac";
    }

    @Override
    public String getBonus() {
        return "+5% Cooldown Reduction but -20% Damage.";
    }

    @Override
    public String getDescription() {
        return "501 ways to end a life painlessly.";
    }

    @Override
    public Classes getClasses() {
        return Classes.ROGUE;
    }
}