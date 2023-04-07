package com.ebicep.warlords.pve.items.types.specialitems.gammagauntlet;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.statpool.StatPool;

import java.util.HashMap;

public class EidolonicClaws extends SpecialGammaGauntlet implements EPSandEPH {

    @Override
    public HashMap<StatPool, Integer> getBonusStats() {
        return EPSandEPH.super.getBonusStats();
    }

    public EidolonicClaws(ItemTier tier) {
        super(tier);
    }

    @Override
    public Classes getClasses() {
        return Classes.WARRIOR;
    }

    @Override
    public String getName() {
        return "Eidolonic Claws";
    }

    @Override
    public String getBonus() {
        return "+5 EPS but -20% EPH";
    }

    @Override
    public String getDescription() {
        return "Only a man who doesn't fear death would touch such an object.";
    }


}
