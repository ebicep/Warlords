package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.gamma;

import com.ebicep.warlords.player.general.Classes;

public class ConquerorsFist extends SpecialGammaGauntlet implements EPSandSpeed {

    @Override
    public Classes getClasses() {
        return Classes.PALADIN;
    }

    @Override
    public String getName() {
        return "Conqueror's Fist";
    }

    @Override
    public String getBonus() {
        return "+5 EPS but -20% Speed.";
    }

    @Override
    public String getDescription() {
        return "Kneel before your ruler, Godfrey of Boullion!";
    }

}
