package com.ebicep.warlords.pve.upgrades.mage.pyromancer;

import com.ebicep.warlords.abilties.Fireball;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class FireballBranch extends AbstractUpgradeBranch<Fireball> {

    double projectileSpeed = ability.getProjectileSpeed();
    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();
    float hitbox = ability.getHitbox();

    public FireballBranch(AbilityTree abilityTree, Fireball ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade(
                "Impair - Tier I",
                "+2.5% Damage",
                5000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.025f);
                    ability.setMaxDamageHeal(maxDamage * 1.025f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "+5% Damage",
                10000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.05f);
                    ability.setMaxDamageHeal(maxDamage * 1.05f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "+7.5% Damage",
                15000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.075f);
                    ability.setMaxDamageHeal(maxDamage * 1.075f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "+10% Damage\n+50% Projectile speed",
                20000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.1f);
                    ability.setMaxDamageHeal(maxDamage * 1.1f);
                    ability.setProjectileSpeed(projectileSpeed * 1.5f);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "+0.25 Blocks hit radius",
                5000,
                () -> {
                    ability.setHitbox(hitbox + 0.25f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "+0.5 Blocks hit radius",
                10000,
                () -> {
                    ability.setHitbox(hitbox + 0.5f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "+0.75 Blocks hit radius",
                15000,
                () -> {
                    ability.setHitbox(hitbox + 0.75f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "+1 Block hit radius\n-5 Energy cost",
                20000,
                () -> {
                    ability.setHitbox(hitbox + 1);
                    ability.setEnergyCost(ability.getEnergyCost() - 5);
                }
        ));

        masterUpgrade = new Upgrade(
                "Fiery Fusillade",
                "Fireball - Master Upgrade",
                "Direct hits apply the BURN status for 5 seconds.\n\nBURN: Enemies take 20% more damage from all sources\nand burn for 0.25% of their max health every second.",
                50000,
                () -> {
                    ability.setPveUpgrade(true);
                }
        );
    }
}
