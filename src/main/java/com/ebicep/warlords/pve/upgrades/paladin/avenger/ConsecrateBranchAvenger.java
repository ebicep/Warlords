package com.ebicep.warlords.pve.upgrades.paladin.avenger;

import com.ebicep.warlords.abilities.ConsecrateAvenger;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.paladin.AbstractConsecrateBranch;

public class ConsecrateBranchAvenger extends AbstractConsecrateBranch<ConsecrateAvenger> {

    public ConsecrateBranchAvenger(AbilityTree abilityTree, ConsecrateAvenger ability) {
        super(abilityTree, ability);

        masterUpgrade2 = new Upgrade(
                "Sanctifying Ring",
                "Consecrate - Master Upgrade",
                """
                        Transform consecrate into a mobile ring that travels with you, damaging nearby enemies every 1.5s in a 4 block radius.
                        Enemies hit by Sanctifying Ring take 30% more damage from your attacks while the ring is active.
                        """,
                50000,
                () -> {
                    ability.setStrikeDamageBoost(30);
                }
        );
    }

}
