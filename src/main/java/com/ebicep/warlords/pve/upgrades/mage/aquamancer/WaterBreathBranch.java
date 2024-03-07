package com.ebicep.warlords.pve.upgrades.mage.aquamancer;

import com.ebicep.warlords.abilities.WaterBreath;
import com.ebicep.warlords.pve.upgrades.*;

public class WaterBreathBranch extends AbstractUpgradeBranch<WaterBreath> {

    double velocity = ability.getVelocity();
    int coneRange = ability.getMaxAnimationTime();
    float hitbox = ability.getHitbox();

    public WaterBreathBranch(AbilityTree abilityTree, WaterBreath ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeHealing(ability, 7.5f)
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
                "Malicious Mist",
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
