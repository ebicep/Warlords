package com.ebicep.warlords.pve.items.types.specialitems.buckler.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

import java.util.Set;

public class AnointedAegis extends SpecialGammaBuckler implements DamageReductionandKBRes, CraftsInto.CraftsCrossNecklaceCharm {

    public AnointedAegis() {
    }

    public AnointedAegis(Set<BasicStatPool> basicStatPools) {
        super(basicStatPools);
    }

    @Override
    public String getName() {
        return "Anointed Aegis";
    }

    @Override
    public String getBonus() {
        return "+5% Damage Reduction but -20% KB-Resistance.";
    }

    @Override
    public String getDescription() {
        return "Smite then Fight, right?";
    }

    @Override
    public Classes getClasses() {
        return Classes.PALADIN;
    }
}