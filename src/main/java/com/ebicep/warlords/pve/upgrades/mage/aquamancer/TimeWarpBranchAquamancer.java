package com.ebicep.warlords.pve.upgrades.mage.aquamancer;

import com.ebicep.warlords.abilities.TimeWarpAquamancer;
import com.ebicep.warlords.pve.upgrades.*;

public class TimeWarpBranchAquamancer extends AbstractUpgradeBranch<TimeWarpAquamancer> {

    int healing = ability.getWarpHealPercentage();

    public TimeWarpBranchAquamancer(AbilityTree abilityTree, TimeWarpAquamancer ability) {
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
                "Monsoon Leap",
                "Time Warp - Master Upgrade",
                "Time Warp can now be re-activated to teleport you back early. Additionally, triple the duration of Time Warp and now leave a water altar " +
                        "that grants allies immunity to debuffs and slowness effects while near it. " +
                        "The altar is removed upon warping back.",
                50000,
                () -> {
                    ability.setTickDuration(ability.getTickDuration() * 3);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Cyclone",
                "Time Warp - Master Upgrade",
                """
                        Time Warp forms a cyclone of water around you as you walk, enemies that are within 3 blocks of you while the ability is active are knocked back.
                        After warping back, outgoing healing is increased by 15% for 5s.
                        """,
                50000,
                () -> {
                }
        );
    }
}
