package com.ebicep.warlords.pve.upgrades.mage.cryomancer;

import com.ebicep.warlords.abilties.FreezingBreath;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class FreezingBreathBranch extends AbstractUpgradeBranch<FreezingBreath> {

    float cooldown = ability.getCooldown();
    int slowness = ability.getSlowness();
    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();

    public FreezingBreathBranch(AbilityTree abilityTree, FreezingBreath ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade(
                "Impair - Tier I",
                "+7.5% Damage",
                5000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.075f);
                    ability.setMaxDamageHeal(maxDamage * 1.075f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "+15% Damage",
                10000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.15f);
                    ability.setMaxDamageHeal(maxDamage * 1.15f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "+22.5% Damage",
                15000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.225f);
                    ability.setMaxDamageHeal(maxDamage * 1.225f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "+30% Damage",
                20000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.3f);
                    ability.setMaxDamageHeal(maxDamage * 1.3f);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "-5% Cooldown reduction\n+4% Slowness",
                5000,
                () -> {
                    ability.setCooldown(cooldown * 0.95f);
                    ability.setSlowness(slowness + 4);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "-10% Cooldown reduction\n+6% Slowness",
                10000,
                () -> {
                    ability.setCooldown(cooldown * 0.9f);
                    ability.setSlowness(slowness + 6);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "-15% Cooldown reduction\n+8% Slowness",
                15000,
                () -> {
                    ability.setCooldown(cooldown * 0.85f);
                    ability.setSlowness(slowness + 7);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "-20% Cooldown reduction\n+10% Slowness",
                20000,
                () -> {
                    ability.setCooldown(cooldown * 0.8f);
                    ability.setSlowness(slowness + 10);
                }
        ));

        masterUpgrade = new Upgrade(
                "Blizzard",
                "Freezing Breath - Master Upgrade",
                "Unleash a blizzard typhoon in front of you,\ndealing 50% more damage.\n\nAdditionally, gain 4% damage reduction for each\nenemy hit, lasts 4 seconds. (up to 20%)",
                50000,
                () -> {
                    ability.setHitbox(ability.getHitbox() * 1.6f);
                    ability.setMaxAnimationTime(ability.getMaxAnimationTime() * 2);
                    ability.setPveUpgrade(true);
                }
        );
    }
}
