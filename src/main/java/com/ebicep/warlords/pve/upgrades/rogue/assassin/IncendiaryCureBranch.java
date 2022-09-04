package com.ebicep.warlords.pve.upgrades.rogue.assassin;

import com.ebicep.warlords.abilties.IncendiaryCurse;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class IncendiaryCureBranch extends AbstractUpgradeBranch<IncendiaryCurse> {

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();
    float hitbox = ability.getHitbox();
    float cooldown = ability.getCooldown();

    public IncendiaryCureBranch(AbilityTree abilityTree, IncendiaryCurse ability) {
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
                "-5% Cooldown reduction\n+0.5 Blocks hit radius",
                5000,
                () -> {
                    ability.setCooldown(cooldown * 0.95f);
                    ability.setHitbox(hitbox + 0.5f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "-10% Cooldown reduction\n+1 Blocks hit radius",
                10000,
                () -> {
                    ability.setCooldown(cooldown * 0.9f);
                    ability.setHitbox(hitbox + 1);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "-15% Cooldown reduction\n+1.5 Blocks hit radius",
                15000,
                () -> {
                    ability.setCooldown(cooldown * 0.85f);
                    ability.setHitbox(hitbox + 1.5f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "-20% Cooldown reduction\n+2 Blocks hit radius",
                20000,
                () -> {
                    ability.setCooldown(cooldown * 0.8f);
                    ability.setHitbox(hitbox + 2);
                }
        ));

        masterUpgrade = new Upgrade(
                "Blazing Curse",
                "Incendiary Curse - Master Upgrade",
                "All enemies hit become disoriented. Increase\nthe damage they take by 50% for 2 seconds.",
                50000,
                () -> {
                    ability.setPveUpgrade(true);
                }
        );
    }
}
