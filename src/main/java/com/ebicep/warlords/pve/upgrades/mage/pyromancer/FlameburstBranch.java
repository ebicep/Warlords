package com.ebicep.warlords.pve.upgrades.mage.pyromancer;

import com.ebicep.warlords.abilties.FlameBurst;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class FlameburstBranch extends AbstractUpgradeBranch<FlameBurst> {

    float cooldown = ability.getCooldown();
    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();
    float critMultiplier = ability.getCritMultiplier();
    float hitbox = ability.getHitbox();

    public FlameburstBranch(AbilityTree abilityTree, FlameBurst ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade(
                "Zeal - Tier I",
                "-5% Cooldown reduction",
                5000,
                () -> {
                    ability.setCooldown(cooldown * 0.95f);
                }
        ));
        treeA.add(new Upgrade(
                "Zeal - Tier II",
                "-10% Cooldown reduction",
                10000,
                () -> {
                    ability.setCooldown(cooldown * 0.9f);
                }
        ));
        treeA.add(new Upgrade(
                "Zeal - Tier III",
                "-15% Cooldown reduction",
                15000,
                () -> {
                    ability.setCooldown(cooldown * 0.85f);
                }
        ));
        treeA.add(new Upgrade(
                "Zeal - Tier IV",
                "-20% Cooldown reduction",
                20000,
                () -> {
                    ability.setCooldown(cooldown * 0.8f);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "+0.5 Blocks hit radius",
                5000,
                () -> {
                    ability.setHitbox(hitbox + 0.5f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "+1 Block hit radius",
                10000,
                () -> {
                    ability.setHitbox(hitbox + 1);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "+1.5 Blocks hit radius",
                15000,
                () -> {
                    ability.setHitbox(hitbox + 1.5f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "+2 Blocks hit radius\n+15% Crit multiplier",
                20000,
                () -> {
                    ability.setHitbox(hitbox + 2);
                    ability.setCritMultiplier(critMultiplier + 15);
                }
        ));

        masterUpgrade = new Upgrade(
                "Flame Awakening",
                "Flame Burst - Master Upgrade",
                "Flame Burst deals significantly more damage and ramps up crit chance, crit multiplier and damage very quickly per blocks traveled at the cost " +
                        "of heavily reduced projectile speed.",
                50000,
                () -> {
                    ability.setProjectileWidth(0.72D);
                    ability.setProjectileSpeed(ability.getProjectileSpeed() * 0.2);
                    ability.setMinDamageHeal(minDamage * 2);
                    ability.setMaxDamageHeal(maxDamage * 2);
                    ability.setHitbox(ability.getHitbox() + 5);
                    ability.setPveUpgrade(true);
                }
        );
    }
}
