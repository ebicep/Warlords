package com.ebicep.warlords.pve.upgrades.warrior.berserker;

import com.ebicep.warlords.abilities.BloodLust;
import com.ebicep.warlords.pve.upgrades.*;

public class BloodlustBranch extends AbstractUpgradeBranch<BloodLust> {

    float healReductionPercent = ability.getHealReductionPercent();

    public BloodlustBranch(AbilityTree abilityTree, BloodLust ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgrade(new UpgradeTypes.HealingUpgradeType() {

                    @Override
                    public String getDescription0(String value) {
                        return "-" + value + "% AOE Healing Reduction";
                    }

                    @Override
                    public void run(float value) {
                        ability.setHealReductionPercent(healReductionPercent + value);
                    }
                }, 3.75f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeCooldown(ability, 0.0375f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Sanguineous",
                "Blood Lust - Master Upgrade",
                """
                        +5s Duration
                        
                        While Blood Lust is active, increase all damage against bleeding or wounded targets by 30%""",
                50000,
                () -> {
                    ability.setTickDuration(ability.getTickDuration() + 100);

                }
        );
        masterUpgrade2 = new Upgrade(
                "Blood Thirsty",
                "Blood Lust - Master Upgrade",
                """
                        While Bloodlust is active, each strike hits again for 20% of the original damage (Additional strikes don't heal with Bloodlust) and enemies affected by BLEED take 30% more damage.
                        """,
                50000,
                () -> {

                }
        );
    }
}
