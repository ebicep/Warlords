package com.ebicep.warlords.pve.upgrades.mage.aquamancer;

import com.ebicep.warlords.abilities.WaterBolt;
import com.ebicep.warlords.pve.upgrades.*;

public class WaterBoltBranch extends AbstractUpgradeBranch<WaterBolt> {

    float minHealing = ability.getMinDamageHeal();
    float maxHealing = ability.getMaxDamageHeal();
    float minDamage = ability.getMinDamage();
    float maxDamage = ability.getMaxDamage();

    public WaterBoltBranch(AbilityTree abilityTree, WaterBolt ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgrade(new UpgradeTypes.HealingUpgradeType() {
                    @Override
                    public void run(float value) {
                        value = 1 + value / 100;
                        ability.setMinDamageHeal(minHealing * value);
                        ability.setMaxDamageHeal(maxHealing * value);
                    }
                }, 15f)
                .addUpgrade(new UpgradeTypes.DamageUpgradeType() {
                    @Override
                    public void run(float value) {
                        value = 1 + value / 100;
                        ability.setMinDamage(minDamage * value);
                        ability.setMaxDamage(maxDamage * value);
                    }
                }, 15f, 3, 4)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeEnergy(ability, 5f)
                .addUpgradeSplash(ability, .5f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Aquatic Assault",
                "Water Bolt - Master Upgrade",
                "+100% Projectile speed\n\nWater Bolt increases the damage dealt of all allies it hits by 10% for 10 seconds.",
                50000,
                () -> {
                    ability.setProjectileSpeed(ability.getProjectileSpeed() * 2);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Hydro Shot",
                "Water Bolt - Master Upgrade",
                """
                        Projectile speed +100%.
                                                
                        Water Bolt direct-hits on enemies and allies are guaranteed crits.
                        """,
                50000,
                () -> {
                    ability.setProjectileSpeed(ability.getProjectileSpeed() * 2);
                }
        );
    }
}
