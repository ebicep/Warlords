package com.ebicep.warlords.pve.upgrades.mage.pyromancer;

import com.ebicep.warlords.abilities.Fireball;
import com.ebicep.warlords.pve.upgrades.*;

public class FireballBranch extends AbstractUpgradeBranch<Fireball> {

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();

    public FireballBranch(AbilityTree abilityTree, Fireball ability) {
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
                .addUpgrade(new UpgradeTypes.UpgradeType() {
                    @Override
                    public String getDescription0(String value) {
                        return "+50% Projectile Speed";
                    }

                    @Override
                    public void run(float value) {
                        ability.setProjectileSpeed(ability.getProjectileSpeed() * 1.5);
                    }
                }, 4)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create()
                .addUpgradeSplash(ability, 0.5f)
                .addUpgradeEnergy(ability, 2.5f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Fiery Fusillade",
                "Fireball - Master Upgrade",
                """
                        Direct-hits apply the BURN status for 5 seconds.

                        BURN: Enemies take 20% more damage from all sources and burn for 0.5% of their max health every second.""",
                50000,
                () -> {

                }
        );

        masterUpgrade2 = new Upgrade(
                "Volatile Flames",
                "Fireball - Master Upgrade",
                """
                        Fires 2 projectiles. Direct-hits apply the SCORCHED status.
                                                
                        SCORCHED: Enemies will take an additional instance of damage after 2 seconds dealing 0.25% of their max health.""",
                50000,
                () -> {
                    ability.setShotsFiredAtATime(2);
                }
        );
    }
}
