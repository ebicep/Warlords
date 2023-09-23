package com.ebicep.warlords.pve.upgrades.mage.cryomancer;

import com.ebicep.warlords.abilities.TimeWarpCryomancer;
import com.ebicep.warlords.pve.upgrades.*;

public class TimeWarpBranchCryomancer extends AbstractUpgradeBranch<TimeWarpCryomancer> {

    int healing = ability.getWarpHealPercentage();

    public TimeWarpBranchCryomancer(AbilityTree abilityTree, TimeWarpCryomancer ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create()
                .addUpgrade(new UpgradeTypes.HealingUpgradeType() {
                    @Override
                    public void run(float value) {
                        ability.setWarpHealPercentage(healing + (int) value);
                    }
                }, 5f)
                .addTo(treeA);


        UpgradeTreeBuilder
                .create()
                .addUpgradeCooldown(ability)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Frostbite Leap",
                "Time Warp - Master Upgrade",
                "Time Warp can now be re-activated to teleport you back early. Additionally, double the duration of Time Warp and summon a cryopod " +
                        "that draws nearby enemies and shatters nearby enemies if destroyed, slowing them by 80% for 3 seconds. " +
                        "Upon warping back, you're granted 80% damage reduction for 5 seconds",
                50000,
                () -> {
                    ability.setTickDuration(ability.getTickDuration() * 2);

                }
        );
        masterUpgrade2 = new Upgrade(
                "Freezing Cold",
                "Time Warp - Master Upgrade",
                """
                        All enemies within a 30 block radius will be slowed by 80% for 5s.
                        Additionally, enemies that are actively being slowed by Time Warp will be more susceptible to damage by 15%.
                        """,
                50000,
                () -> {
                }
        );
    }
}
