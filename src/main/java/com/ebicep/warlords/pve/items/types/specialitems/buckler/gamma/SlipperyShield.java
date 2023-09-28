package com.ebicep.warlords.pve.items.types.specialitems.buckler.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

import java.util.Set;

public class SlipperyShield extends SpecialGammaBuckler implements DamageReductionandRegenTimer, CraftsInto.CraftsBucklerPiece {

    public SlipperyShield() {
    }

    public SlipperyShield(Set<BasicStatPool> basicStatPools) {
        super(basicStatPools);
    }

    @Override
    public String getName() {
        return "Slippery Shield";
    }

    @Override
    public String getBonus() {
        return "+5% Damage Reduction but -20% Regen Timer.";
    }

    @Override
    public String getDescription() {
        return "Slick surfaces never worked so well!";
    }

    @Override
    public Classes getClasses() {
        return Classes.MAGE;
    }

}