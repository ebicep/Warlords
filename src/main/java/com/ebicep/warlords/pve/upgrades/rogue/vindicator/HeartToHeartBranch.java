package com.ebicep.warlords.pve.upgrades.rogue.vindicator;

import com.ebicep.warlords.abilities.HeartToHeart;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeTreeBuilder;

public class HeartToHeartBranch extends AbstractUpgradeBranch<HeartToHeart> {

    public HeartToHeartBranch(AbilityTree abilityTree, HeartToHeart ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeHitBox(ability, 2f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeCooldown(ability)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Heart of Hearts",
                "Heart To Heart - Master Upgrade",
                "Heart To Heart now deals 1635 - 2096 damage to all enemies you pass through. Enemies hit are stunned for 1 second.",
                50000,
                () -> {

                }
        );
        masterUpgrade2 = new Upgrade(
                "Heart in Hearts",
                "Heart To Heart - Master Upgrade",
                """
                        +10 Additional Block Radius
                                                
                        For every block traveled, gain a 3% damage reduction for 6s.
                        """,
                50000,
                () -> {
                    ability.getHitBoxRadius().addAdditiveModifier("Master Upgrade Branch", 10);
                }
        );
    }
}
