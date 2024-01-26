package com.ebicep.warlords.pve.upgrades.paladin.protector;

import com.ebicep.warlords.abilities.HammerOfLight;
import com.ebicep.warlords.pve.upgrades.*;

public class HammerOfLightBranch extends AbstractUpgradeBranch<HammerOfLight> {

    int duration = ability.getTickDuration();
    float minHealing = ability.getMinDamageHeal();
    float maxHealing = ability.getMaxDamageHeal();
    float minDamage = ability.getMinDamage();
    float maxDamage = ability.getMaxDamage();

    public HammerOfLightBranch(AbilityTree abilityTree, HammerOfLight ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgrade(new UpgradeTypes.DamageUpgradeType() {
                    @Override
                    public void run(float value) {
                        value = 1 + value / 100;
                        ability.setMinDamage(minDamage * value);
                        ability.setMaxDamage(maxDamage * value);
                    }
                }, 7.5f)
                .addUpgrade(new UpgradeTypes.HealingUpgradeType() {
                    @Override
                    public void run(float value) {
                        value = 1 + value / 100;
                        ability.setMinDamageHeal(minHealing * value);
                        ability.setMaxDamageHeal(maxHealing * value);
                    }
                }, 7.5f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeCooldown(ability)
                .addUpgrade(
                        new UpgradeTypes.DurationUpgradeType() {
                            @Override
                            public void run(float value) {
                                ability.setTickDuration((int) (duration + value));
                            }
                        }, 40f, 4
                )
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Hammer of Illusion",
                "Hammer of Light - Master Upgrade",
                "Upon activating Crown of Light, release 4 additional light rays that deal quintuple the damage to all nearby enemies and heal allies for " +
                        "the same amount.",
                50000,
                () -> {

                }
        );
        masterUpgrade2 = new Upgrade(
                "Hammer of Disillusion",
                "Hammer of Light - Master Upgrade",
                """
                        +20% Cooldown Reduction
                        +3 Block Radius
                                                
                        Hammer of Light/Crown of Light now grants debuff immunity. Additionally, enemies within the radius will take 10% more damage.
                        """,
                50000,
                () -> {
                    ability.getRadius().addAdditiveModifier("Master Upgrade Branch", 3);
                }
        );
    }
}
