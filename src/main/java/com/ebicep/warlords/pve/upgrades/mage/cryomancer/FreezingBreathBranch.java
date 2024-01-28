package com.ebicep.warlords.pve.upgrades.mage.cryomancer;

import com.ebicep.warlords.abilities.FreezingBreath;
import com.ebicep.warlords.pve.upgrades.*;

import javax.annotation.Nullable;

public class FreezingBreathBranch extends AbstractUpgradeBranch<FreezingBreath> {

    int slowness = ability.getSlowness();
    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();

    public FreezingBreathBranch(AbilityTree abilityTree, FreezingBreath ability) {
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
                }, 7.5f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeCooldown(ability)
                .addUpgrade(new UpgradeTypes.UpgradeType() {

                    @Nullable
                    @Override
                    public String getDescription(double value) {
                        return UpgradeTypes.UpgradeType.super.getDescription(value + 2);
                    }

                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + "% Slowness";
                    }

                    @Override
                    public void run(float value) {
                        ability.setSlowness(slowness + (int) value + 2);
                    }
                }, 2f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Blizzard",
                "Freezing Breath - Master Upgrade",
                """
                        Unleash a blizzard typhoon in front of you, dealing 50% more damage.
                                
                        Additionally, gain 5% damage reduction for each enemy hit, lasts 4 seconds. (up to 30%)
                        """,
                50000,
                () -> {
                    ability.multiplyMinMax(1.5f);
                    ability.setHitbox(ability.getHitbox() * 1.6f);
                    ability.setMaxAnimationTime(ability.getMaxAnimationTime() * 2);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Cold Front",
                "Freezing Breath - Master Upgrade",
                """
                        Condense the breath into a ball of ice and snow, now a projectile, will explode on impact. Enemies hit will be CHILLED for 4s. Damage increases based on blocks traveled.
                                                
                        CHILLED: Become slowed by 50% and deal 25% less damage.
                        """,
                50000,
                () -> {
                    ability.multiplyMinMax(1.5f);
                    ability.setHitbox(ability.getHitbox() * 1.6f);
                    ability.setMaxAnimationTime(ability.getMaxAnimationTime() * 2);
                }
        );
    }
}
