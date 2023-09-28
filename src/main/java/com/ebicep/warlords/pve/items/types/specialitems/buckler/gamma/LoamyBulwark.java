package com.ebicep.warlords.pve.items.types.specialitems.buckler.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

import java.util.Set;

public class LoamyBulwark extends SpecialGammaBuckler implements DamageReductionandRegenTimer, CraftsInto.CraftsAerialAegis {

    public LoamyBulwark() {
    }

    public LoamyBulwark(Set<BasicStatPool> basicStatPools) {
        super(basicStatPools);
    }

    @Override
    public String getName() {
        return "Loamy Bulwark";
    }

    @Override
    public String getBonus() {
        return "+5% Damage Reduction but -20% Regen Timer.";
    }

    @Override
    public String getDescription() {
        return "Basically a rock on a stick.";
    }

    @Override
    public Classes getClasses() {
        return Classes.SHAMAN;
    }

}