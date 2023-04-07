package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.statpool.StatPool;

import java.util.HashMap;

public class PatrioticClaws extends SpecialGammaGauntlet implements EPSandSpeed {

    @Override
    public HashMap<StatPool, Integer> getBonusStats() {
        return EPSandSpeed.super.getBonusStats();
    }

    public PatrioticClaws(ItemTier tier) {
        super(tier);
    }

    @Override
    public Classes getClasses() {
        return Classes.WARRIOR;
    }

    @Override
    public String getName() {
        return "Patriotic Claws";
    }

    @Override
    public String getBonus() {
        return "+5 EPS but -20% Speed.";
    }

    @Override
    public String getDescription() {
        return "Only someone in the depths of despair would accept such a gift.";
    }

}
