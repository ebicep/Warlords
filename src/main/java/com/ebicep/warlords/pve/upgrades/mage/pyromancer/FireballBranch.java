package com.ebicep.warlords.pve.upgrades.mage.pyromancer;

import com.ebicep.warlords.abilities.Fireball;
import com.ebicep.warlords.pve.upgrades.*;

public class FireballBranch extends AbstractUpgradeBranch<Fireball> {

    public FireballBranch(AbilityTree abilityTree, Fireball ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDamage(ability, 7.5f, 1, 2, 3)
                .addUpgradeDamage(ability, 40f, 4)
                .addUpgrade(new UpgradeTypes.UpgradeType() {
                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + "% Projectile Speed";
                    }

                    @Override
                    public void run(float value) {
                        value = 1 + value / 100;
                        ability.setProjectileSpeed(ability.getProjectileSpeed() * value);
                    }
                }, 50f, 4)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeSplash(ability, 0.5f)
                .addUpgradeEnergy(ability, 3.75f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Fiery Fusillade",
                "Fireball - Master Upgrade",
                """
                        Direct-hits apply the BURN status for 5s.

                        BURN: Enemies take 20% more damage from all sources and burn for 0.5% of their max health every second.""",
                50000,
                () -> {

                }
        );

        masterUpgrade2 = new Upgrade(
                "Volatile Flames",
                "Fireball - Master Upgrade",
                """
                        Fires 2 projectiles. Direct-hits apply the IGNITE status.
                                                
                        IGNITE: Enemies will explode after 1s dealing 450-650 true damage to nearby enemies.""",
                50000,
                () -> {
                    ability.setShotsFiredAtATime(2);
                }
        );
    }
}
