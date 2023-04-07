package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.statpool.StatPool;

import java.util.HashMap;

public class ArchangelsFist extends SpecialGammaGauntlet implements EPSandMaxEnergy {

    public ArchangelsFist(ItemTier tier) {
        super(tier);
    }

    @Override
    public HashMap<StatPool, Integer> getBonusStats() {
        return EPSandMaxEnergy.super.getBonusStats();
    }

    @Override
    public Classes getClasses() {
        return Classes.PALADIN;
    }

    @Override
    public String getName() {
        return "Archangel's Fist";
    }

    @Override
    public String getBonus() {
        return "Increase energy gain by 3 per second but reduces energy cap by 20%.";
    }

    @Override
    public String getDescription() {
        return "Behold! Judgement from on high!";
    }

}
