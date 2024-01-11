package com.ebicep.warlords.pve.upgrades.rogue.vindicator;

import com.ebicep.warlords.abilities.PrismGuard;
import com.ebicep.warlords.pve.upgrades.*;

public class PrismGuardBranch extends AbstractUpgradeBranch<PrismGuard> {

    int bubbleHealing = ability.getBubbleHealing();
    float bubbleMissingHealing = ability.getBubbleMissingHealing();

    @Override
    public void runOnce() {
        ability.setTickDuration(120);
        ability.setProjectileDamageReduction(75);
    }

    public PrismGuardBranch(AbilityTree abilityTree, PrismGuard ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgrade(new UpgradeTypes.HealingUpgradeType() {

                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + " Base Healing";
                    }

                    @Override
                    public void run(float value) {
                        ability.setBubbleHealing((int) (bubbleHealing + value));
                    }
                }, 100f)
                .addUpgrade(new UpgradeTypes.HealingUpgradeType() {

                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + "% Missing Health Healing";
                    }

                    @Override
                    public void run(float value) {
                        ability.setBubbleMissingHealing(bubbleMissingHealing + value);
                    }
                }, 1f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDuration(ability, 20f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Deflective Tesseract",
                "Prism Guard - Master Upgrade",
                "Gain 10% damage reduction and enemies in a 15 block radius will target you while Prism Guard is active. Additionally, double the range of Prism Guard and increase your passive damage reduction by 25%",
                50000,
                () -> {
                    abilityTree.getWarlordsPlayer().setDamageResistance(abilityTree.getWarlordsPlayer().getSpec().getDamageResistance() + 25);
                    ability.setBubbleRadius(ability.getBubbleRadius() * 2);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Alacrity",
                "Prism Guard - Master Upgrade",
                """
                        Double the radius of Prism Guard and reduce the melee damage of enemies in the radius by 25%. Additionally, gain knockback immunity while protected by Prism Guard.
                        """,
                50000,
                () -> {
                    ability.setBubbleRadius(ability.getBubbleRadius() * 2);
                }
        );
    }
}
