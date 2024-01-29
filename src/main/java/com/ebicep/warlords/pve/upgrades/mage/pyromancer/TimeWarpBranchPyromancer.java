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
                "Time Warp can now be re-activated to teleport you back early. Additionally, gain 1% Crit Chance and Crit" +
                        " Multiplier for each block travelled while Time Warp is active and double the duration of Time Warp.",
                50000,
                () -> {
                    ability.setTickDuration(ability.getTickDuration() * 2);

                }
        );
        masterUpgrade2 = new Upgrade(
                "Accursed Leap",
                "Time Warp - Master Upgrade",
                """
                        While Time Warp is active, enemies you run into will become linked.
                        When warping back, all linked enemies will combust dealing 5% of their max hp as damage.
                        For every linked enemy that dies prior to completing the warp will reduce Time Warp cooldown by .75s.
                        """,
                50000,
                () -> {

                }
        );
    }
}
