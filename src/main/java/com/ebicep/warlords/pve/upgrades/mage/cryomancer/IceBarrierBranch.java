package com.ebicep.warlords.pve.upgrades.mage.cryomancer;

import com.ebicep.warlords.abilities.IceBarrier;
import com.ebicep.warlords.pve.upgrades.*;

public class IceBarrierBranch extends AbstractUpgradeBranch<IceBarrier> {

    float damageReductionPercent = ability.getDamageReductionPercent();

    public IceBarrierBranch(AbilityTree abilityTree, IceBarrier ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgrade(new UpgradeTypes.ShieldUpgradeType() {
                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + "% Damage Reduction";
                    }

                    @Override
                    public void run(float value) {
                        ability.setDamageReductionPercent(damageReductionPercent + value);
                    }
                }, 7.5f)
                .addUpgradeCooldown(ability, 0.1f, 4)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDuration(ability)
                .addUpgrade(new UpgradeTypes.UpgradeType() {
                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + "% Movement Speed Reduction";
                    }

                    @Override
                    public void run(float value) {
                        ability.setSlownessOnMeleeHit((int) (ability.getSlownessOnMeleeHit() + value));
                    }
                }, 20f, 4)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Aggravating Hailstorm",
                "Ice Barrier - Master Upgrade",
                "Surround yourself in a glacial super shield, gaining 30% knockback resistance and slowing all nearby enemies by 75%." +
                        " Additionally, reduce their damage reduction by 1% for every 0.25 seconds in your glacial shield.",
                50000,
                () -> {

                }
        );
        masterUpgrade2 = new Upgrade(
                "Ice Wall",
                "Ice Barrier - Master Upgrade",
                """
                        Ice Barrier has been converted into a projected wall of ice. Enemies that pass through this wall will be slowed by 50% and take 35% more damage from all sources for the duration of Ice Barrier.
                        """,
                50000,
                () -> {

                }
        );
    }
}
