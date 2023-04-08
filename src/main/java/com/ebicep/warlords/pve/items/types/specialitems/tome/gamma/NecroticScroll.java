package com.ebicep.warlords.pve.items.types.specialitems.tome.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

public class NecroticScroll extends SpecialGammaTome implements CDRandCritChance, CraftsInto.CraftsPansTome {

    @Override
    public String getName() {
        return "Necrotic Scroll";
    }

    @Override
    public String getBonus() {
        return "+5% Cooldown Reduction but -20% Crit Chance.";
    }

    @Override
    public String getDescription() {
        return "Unlock the mysteries of the dead...";
    }

    @Override
    public Classes getClasses() {
        return Classes.SHAMAN;
    }

}
