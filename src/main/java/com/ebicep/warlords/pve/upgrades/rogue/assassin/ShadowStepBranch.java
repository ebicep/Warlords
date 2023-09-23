package com.ebicep.warlords.pve.upgrades.rogue.assassin;

import com.ebicep.warlords.abilities.ShadowStep;
import com.ebicep.warlords.pve.upgrades.*;

public class ShadowStepBranch extends AbstractUpgradeBranch<ShadowStep> {

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();

    public ShadowStepBranch(AbilityTree abilityTree, ShadowStep ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create()
                .addUpgrade(new UpgradeTypes.DamageUpgradeType() {
                    @Override
                    public void run(float value) {
                        float v = 1 + value / 100;
                        ability.setMinDamageHeal(minDamage * v);
                        ability.setMaxDamageHeal(maxDamage * v);
                    }
                }, 7.5f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create()
                .addUpgradeCooldown(ability)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Shadow Stagger",
                "Shadow Step - Master Upgrade",
                "Gain 80% speed and knockback resistance upon landing for 5 seconds.",
                50000,
                () -> {

                }
        );
        masterUpgrade2 = new Upgrade(
                "Shadow Dash",
                "Shadow Step - Master Upgrade",
                """
                        -10% Cooldown Reduction
                         
                        Instead of leaping forward, instantly dash forward +8 blocks, dealing damage to enemies passed through.
                        """,
                50000,
                () -> {
                    ability.getCooldown().addMultiplicativeModifierMult("Shadow Dash", 0.9f);
                }
        );
    }
}
