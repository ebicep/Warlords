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
                        Transform consecrate into a mobile ring that travels with you, damaging nearby enemies every 1.5s in a 4 block radius.
                        Enemies hit by Sanctifying Ring will grant the you 15 energy, max 100 per proc.
                        """,
                50000,
                () -> {

                }
        );
    }

}
