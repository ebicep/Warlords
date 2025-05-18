package com.ebicep.warlords.pve.upgrades.shaman.thunderlord;

import com.ebicep.warlords.abilities.LightningBolt;
import com.ebicep.warlords.pve.upgrades.*;

import javax.annotation.Nonnull;

public class LightningBoltBranch extends AbstractUpgradeBranch<LightningBolt> {

    float projectileSpeed = ability.getProjectileSpeed();

    public LightningBoltBranch(AbilityTree abilityTree, LightningBolt ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDamage(ability.getDamageValues().getBoltDamage(), 12.5f)
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
                        -10 Energy cost
                        
                        Lightning Bolt shoots two additional projectiles.""",
                50000,
                () -> {
                    ability.getEnergyCost().addAdditiveModifier("Master Upgrade Branch", -10);
                    ability.setShotsFiredAtATime(3);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Electric Bolt",
                "Lightning Bolt - Master Upgrade",
                """
                        -10 Energy cost
                        
                        The first target hit takes 35% more damage enemies hit afterwards take 15% more damage.
                        """,
                50000,
                () -> {
                    ability.getEnergyCost().addAdditiveModifier("Master Upgrade Branch", -10);
                }
        );
    }


}
