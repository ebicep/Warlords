package com.ebicep.warlords.pve.items.types.specialitems.buckler.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

import java.util.Set;

public class OvergrownBuckler extends SpecialGammaBuckler implements DamageReductionandRegenTimer, CraftsInto.CraftsShieldOfSnatching {

    public OvergrownBuckler() {
    }

    public OvergrownBuckler(Set<BasicStatPool> basicStatPools) {
        super(basicStatPools);
    }

    @Override
    public String getName() {
        return "Overgrown Buckler";
    }

    @Override
    public String getBonus() {
        return "+5% Damage Reduction but -20% Regen Timer.";
    }

    @Override
    public String getDescription() {
        return "Some might even say it grew onto there.";
    }

    @Override
    public Classes getClasses() {
        return Classes.ROGUE;
    }

}