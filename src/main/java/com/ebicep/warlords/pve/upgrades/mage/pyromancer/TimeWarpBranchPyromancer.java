package com.ebicep.warlords.pve.upgrades.mage.pyromancer;

import com.ebicep.warlords.abilities.TimeWarpPyromancer;
import com.ebicep.warlords.pve.upgrades.*;

public class TimeWarpBranchPyromancer extends AbstractUpgradeBranch<TimeWarpPyromancer> {

    int healing = ability.getWarpHealPercentage();

    public TimeWarpBranchPyromancer(AbilityTree abilityTree, TimeWarpPyromancer ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgrade(new UpgradeTypes.HealingUpgradeType() {
                    @Override
                    public void run(float value) {
                        ability.setWarpHealPercentage(healing + (int) value);
                    }
                }, 5f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeCooldown(ability)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Infernal Leap",
                "Time Warp - Master Upgrade",
                """
                        Time Warp can now be re-activated to teleport you back early.
                        Additionally, gain 0.75% damage for each block travelled while Time Warp is active and double the duration of Time Warp.""",
                50000,
                () -> {
                    ability.setTickDuration(ability.getTickDuration() * 2);

                }
        );
        masterUpgrade2 = new Upgrade(
                "Accursed Leap",
                "Time Warp - Master Upgrade",
                """
                        After warping back, enemies within 12 blocks will combust dealing 7.5% of their max hp as damage.
                        The cooldown of Time Warp is reduced by .75s for each enemy killed.
                        """,
                50000,
                () -> {

                }
        );
    }
}
