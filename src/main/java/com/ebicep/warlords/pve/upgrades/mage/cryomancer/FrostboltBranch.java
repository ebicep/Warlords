package com.ebicep.warlords.pve.upgrades.mage.cryomancer;

import com.ebicep.warlords.abilities.FrostBolt;
import com.ebicep.warlords.pve.upgrades.*;

public class FrostboltBranch extends AbstractUpgradeBranch<FrostBolt> {

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();

    public FrostboltBranch(AbilityTree abilityTree, FrostBolt ability) {
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
                .addUpgradeEnergy(ability, 2.5f)
                .addUpgradeSplash(ability, .5f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Shatter Bolt",
                "Frostbolt - Master Upgrade",
                "Directly-hit enemies shatter after 1.5 seconds, dealing 409 - 554 damage to all nearby enemies and slow them by 50% for 2 seconds.",
                50000,
                () -> {

                }
        );
        masterUpgrade2 = new Upgrade(
                "Splintered Ice",
                "Frostbolt - Master Upgrade",
                """
                        Now fires Icicles, becoming piercing shots, all enemies hit have their movement speed reduced by 25% for 2 seconds.
                        Additionally, the first enemy hit by an icicle will take 15% more damage.
                        """,
                50000,
                () -> {
                    ability.getSplashRadius().addAdditiveModifier("Master Upgrade Branch", 1.5f);
                }
        );
    }
}
