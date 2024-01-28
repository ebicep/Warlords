package com.ebicep.warlords.pve.upgrades.shaman.thunderlord;

import com.ebicep.warlords.abilities.LightningBolt;
import com.ebicep.warlords.pve.upgrades.*;

import javax.annotation.Nonnull;

public class LightningBoltBranch extends AbstractUpgradeBranch<LightningBolt> {

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();
    double projectileSpeed = ability.getProjectileSpeed();

    public LightningBoltBranch(AbilityTree abilityTree, LightningBolt ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgrade(new UpgradeTypes.DamageUpgradeType() {
                    @Override
                    public void run(float value) {
                        float v = 1 + value / 100;
                        ability.setMinDamageHeal(minDamage * v);
                        ability.setMaxDamageHeal(maxDamage * v);
                    }
                }, 12.5f)
                .addUpgrade(new UpgradeTypes.UpgradeType() {
                    @Nonnull
                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + "% Projectile Speed";
                    }

                    @Override
                    public void run(float value) {
                        float v = 1 + value / 100;
                        ability.setProjectileSpeed(projectileSpeed * v);
                    }
                }, 20f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeEnergy(ability, 2.5f)
                .addUpgradeHitBox(ability, .25f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Lightning Volley",
                "Lightning Bolt - Master Upgrade",
                """
                        -20 Energy cost
                                                
                        Lightning Bolt shoots two additional projectiles.""",
                50000,
                () -> {
                    ability.getEnergyCost().addAdditiveModifier("Master Upgrade Branch", -20);
                    ability.setShotsFiredAtATime(3);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Electric Bolt",
                "Lightning Bolt - Master Upgrade",
                """
                        -20 Energy cost
                                                
                        Each additional enemy hit takes 20% more damage. Max of 5 additional enemies will receive this increase in damage, further enemies will be hit the same as the first.
                        """,
                50000,
                () -> {
                    ability.getEnergyCost().addAdditiveModifier("Master Upgrade Branch", -20);
                }
        );
    }


}
