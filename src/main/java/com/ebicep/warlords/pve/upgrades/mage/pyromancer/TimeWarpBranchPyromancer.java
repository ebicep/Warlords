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
                        
                        Additionally, gain 1.25% damage for each block travelled, when Time Warp ends gain the accumulated damage buff for 8 seconds.""",
                50000,
                () -> {

                }
        );
        masterUpgrade2 = new Upgrade(
                "Accursed Leap",
                "Time Warp - Master Upgrade",
                """
                        -2s warp duration
                        
                        After warping back, enemies within 12 blocks will combust dealing 7.5% of their max hp as damage.
                        The cooldown of Time Warp is reduced by 2s for each enemy killed.
                        """,
                50000,
                () -> {
                    ability.setTickDuration(ability.getTickDuration() - 40);
                }
        );
    }
}
