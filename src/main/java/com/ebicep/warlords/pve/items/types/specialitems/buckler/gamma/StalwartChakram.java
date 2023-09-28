package com.ebicep.warlords.pve.items.types.specialitems.buckler.gamma;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;

import java.util.Set;

public class StalwartChakram extends SpecialGammaBuckler implements DamageReductionandAggroPrio, CraftsInto.CraftsPridwensBulwark {

    public StalwartChakram() {
    }

    public StalwartChakram(Set<BasicStatPool> basicStatPools) {
        super(basicStatPools);
    }

    @Override
    public String getName() {
        return "Stalwart Chakram";
    }

    @Override
    public String getBonus() {
        return "+5% Damage Reduction but -5 Aggro Priority.";
    }

    @Override
    public String getDescription() {
        return "Definitely NOT too heavy to throw.";
    }

    @Override
    public Classes getClasses() {
        return Classes.WARRIOR;
    }

}