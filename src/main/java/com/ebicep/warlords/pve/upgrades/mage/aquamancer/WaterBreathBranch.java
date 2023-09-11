package com.ebicep.warlords.pve.upgrades.mage.aquamancer;

import com.ebicep.warlords.abilities.WaterBreath;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class WaterBreathBranch extends AbstractUpgradeBranch<WaterBreath> {

    float cooldown = ability.getCooldown();
    double velocity = ability.getVelocity();
    float minHealing = ability.getMinDamageHeal();
    float maxHealing = ability.getMaxDamageHeal();
    int coneRange = ability.getMaxAnimationTime();
    float hitbox = ability.getHitbox();

    public WaterBreathBranch(AbilityTree abilityTree, WaterBreath ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade(
                "Impair - Tier I",
                "+7.5% Healing",
                5000,
                () -> {
                    ability.setMinDamageHeal(minHealing * 1.075f);
                    ability.setMaxDamageHeal(maxHealing * 1.075f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "+15% Healing",
                10000,
                () -> {
                    ability.setMinDamageHeal(minHealing * 1.15f);
                    ability.setMaxDamageHeal(maxHealing * 1.15f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "+22.5% Healing",
                15000,
                () -> {
                    ability.setMinDamageHeal(minHealing * 1.225f);
                    ability.setMaxDamageHeal(maxHealing * 1.225f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "+30% Healing",
                20000,
                () -> {
                    ability.setMinDamageHeal(minHealing * 1.3f);
                    ability.setMaxDamageHeal(maxHealing * 1.3f);
                }
        ));

        treeB.add(new Upgrade(
                "Zeal - Tier I",
                "+15% Knockback\n+15% Cone range",
                5000,
                () -> {
                    ability.setVelocity(velocity * 1.15);
                    ability.setHitbox(hitbox + 2);
                    ability.setMaxAnimationTime(coneRange + 4);
                }
        ));
        treeB.add(new Upgrade(
                "Zeal - Tier II",
                "+30% Knockback\n+30% Cone range",
                10000,
                () -> {
                    ability.setVelocity(velocity * 1.3);
                    ability.setHitbox(hitbox + 4);
                    ability.setMaxAnimationTime(coneRange + 8);
                }
        ));
        treeB.add(new Upgrade(
                "Zeal - Tier III",
                "+45% Knockback\n+45% Cone range",
                15000,
                () -> {
                    ability.setVelocity(velocity * 1.45);
                    ability.setHitbox(hitbox + 6);
                    ability.setMaxAnimationTime(coneRange + 12);
                }
        ));
        treeB.add(new Upgrade(
                "Zeal - Tier IV",
                "+60% Knockback\n+60% Cone range",
                20000,
                () -> {
                    ability.setVelocity(velocity * 1.6);
                    ability.setHitbox(hitbox + 8);
                    ability.setMaxAnimationTime(coneRange + 16);
                }
        ));

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
                "Typhoon",
                "Water Breath - Master Upgrade",
                """
                        Additional cone range +100%.
                                                
                        All allies hit by Breath are healed by 2% of their max hp per second for 5s. Enemies pushed by breath will have all active buffs/abilities removed and be unable to receive/use them for 3s.
                        """,
                50000,
                () -> {
                    ability.setMaxAnimationTime(ability.getMaxAnimationTime() * 2);
                    ability.setHitbox(ability.getHitbox() * 2);
                    ability.setMaxAnimationEffects(8);

                }
        );
    }
}
