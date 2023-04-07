package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.gamma;

import com.ebicep.warlords.player.general.Classes;

public class VoltageGauntlet extends SpecialGammaGauntlet implements EPSandMaxEnergy {

    @Override
    public Classes getClasses() {
        return Classes.SHAMAN;
    }

    @Override
    public String getName() {
        return "Voltage Gauntlet";
    }

    @Override
    public String getBonus() {
        return "+5 EPS but -20% Max NRG.";
    }

    @Override
    public String getDescription() {
        return "One touch and you're toast.";
    }

}
