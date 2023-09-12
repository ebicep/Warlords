package com.ebicep.warlords.pve.upgrades.paladin.crusader;

import com.ebicep.warlords.abilities.ConsecrateCrusader;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.paladin.AbstractConsecrateBranch;

public class ConsecrateBranchCrusader extends AbstractConsecrateBranch<ConsecrateCrusader> {

    public ConsecrateBranchCrusader(AbilityTree abilityTree, ConsecrateCrusader ability) {
        super(abilityTree, ability);

        masterUpgrade2 = new Upgrade(
                "Sanctifying Ring",
                "Consecrate - Master Upgrade",
                """
                        Creates a ring around the Crusader that deals 158.4 - 213.6 damage to nearby enemies every 1.5s in a 4 block radius, the ring travels with the Avenger. 
                        Enemies hit by Sanctifying Ring will grant the Crusader 15 energy.
                        """,
                50000,
                () -> {

                }
        );
    }

}
