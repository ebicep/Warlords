package com.ebicep.warlords.pve.upgrades.mage.aquamancer;

import com.ebicep.warlords.abilities.WaterBreath;
import com.ebicep.warlords.pve.upgrades.*;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

public class WaterBreathBranch extends AbstractUpgradeBranch<WaterBreath> {

    double velocity = ability.getVelocity();
    int coneRange = ability.getMaxAnimationTime();
    float hitbox = ability.getHitbox();

    public WaterBreathBranch(AbilityTree abilityTree, WaterBreath ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeHealing(ability.getHealValues().getBreathHealing(), 7.5f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgrade(new UpgradeTypes.NamedUpgradeType() {
                    @Override
                    public String getName() {
                        return "Force";
                    }

                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + "% Knockback\n" +
                                "+" + value + "% Cone range";
                    }

                    @Override
                    public void run(float value) {
                        ability.setVelocity(velocity * (1 + value / 100));
                        int level = Math.round(value / 15);
                        ability.setHitbox(hitbox + level * 2);
                        ability.setMaxAnimationTime(coneRange + level * 4);
                    }
                }, 15f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Typhoon",
                "Water Breath - Master Upgrade",
                "+100% Additional cone range\n\nAll allies hit by Water Breath have their cooldowns reduced by 1.5 seconds and are healed for 2% of their max health per second for 5 seconds.",
                50000,
                () -> {
                    ability.setMaxAnimationTime(ability.getMaxAnimationTime() * 2);
                    ability.setHitbox(ability.getHitbox() * 2);
                    ability.setMaxAnimationEffects(8);

                }
        );
        masterUpgrade2 = new Upgrade(
                "Bubble Blessing",
                "Water Breath - Master Upgrade",
                """
                        +15% Cooldown reduction
                        
                        Healing allies grants them knockback immunity for 4 seconds. Additionally, allies hit by Water Breath are given Bubble Blessing, causing all their attacks (excluding DoT) to have a 35% chance to hit enemies for an additional 372-441 damage.
                        """,
                50000,
                () -> {
                    ability.getCooldown().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, "Bubble Blessing", 0.85f);
                }
        );
    }
}
