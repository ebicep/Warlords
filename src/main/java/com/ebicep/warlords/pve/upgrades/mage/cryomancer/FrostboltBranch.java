package com.ebicep.warlords.pve.upgrades.mage.cryomancer;

import com.ebicep.warlords.abilities.FrostBolt;
import com.ebicep.warlords.pve.upgrades.*;

public class FrostboltBranch extends AbstractUpgradeBranch<FrostBolt> {

    public FrostboltBranch(AbilityTree abilityTree, FrostBolt ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDamage(ability.getDamageValues().getBoltDamage(), 7.f)
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
