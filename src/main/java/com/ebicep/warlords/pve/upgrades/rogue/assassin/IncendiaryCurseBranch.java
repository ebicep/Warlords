package com.ebicep.warlords.pve.upgrades.rogue.assassin;

import com.ebicep.warlords.abilities.IncendiaryCurse;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeTreeBuilder;

public class IncendiaryCurseBranch extends AbstractUpgradeBranch<IncendiaryCurse> {

    @Override
    public void runOnce() {
        ability.getEnergyCost().setBaseValue(40);
    }

    public IncendiaryCurseBranch(AbilityTree abilityTree, IncendiaryCurse ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDamage(ability.getDamageValues().getCurseDamage(), 7.5f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeCooldown(ability)
                .addUpgradeHitBox(ability, .5f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Blazing Curse",
                "Incendiary Curse - Master Upgrade",
                "All enemies hit become disoriented. Increase the damage they take by 50% for 5 seconds.",
                50000,
                () -> {

                }
        );
        masterUpgrade2 = new Upgrade(
                "Unforeseen Curse",
                "Incendiary Curse - Master Upgrade",
                """
                        Increase the stun duration by 2s. Additionally, every enemy stunned gives 5 energy (Max 50).
                        """,
                50000,
                () -> {
                    ability.setBlindDurationInTicks(ability.getBlindDurationInTicks() + 40);
                }
        );
    }
}
