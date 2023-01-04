package com.ebicep.warlords.pve.upgrades.shaman.earthwarden;

import com.ebicep.warlords.abilties.Boulder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class BoulderBranch extends AbstractUpgradeBranch<Boulder> {

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();
    float energyCost = ability.getEnergyCost();
    double velocity = ability.getVelocity();
    double hitbox = ability.getHitbox();

    public BoulderBranch(AbilityTree abilityTree, Boulder ability) {
        super(abilityTree, ability);

        treeA.add(new Upgrade(
                "Impair - Tier I",
                "+10% Damage",
                5000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.1f);
                    ability.setMaxDamageHeal(maxDamage * 1.1f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "+20% Damage",
                10000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.2f);
                    ability.setMaxDamageHeal(maxDamage * 1.2f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "+30% Damage",
                15000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.3f);
                    ability.setMaxDamageHeal(maxDamage * 1.3f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "+40% Damage",
                20000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.4f);
                    ability.setMaxDamageHeal(maxDamage * 1.4f);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "-5 Energy cost",
                5000,
                () -> {
                    ability.setEnergyCost(energyCost - 5);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "-10 Energy cost",
                10000,
                () -> {
                    ability.setEnergyCost(energyCost - 10);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "-15 Energy cost",
                15000,
                () -> {
                    ability.setEnergyCost(energyCost - 15);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "-20 Energy cost",
                20000,
                () -> {
                    ability.setEnergyCost(energyCost - 20);
                }
        ));

        masterUpgrade = new Upgrade(
                "Terrestrial Meteor",
                "Boulder - Master Upgrade",
                "Boulder throws upwards, deals 4x times the damage and increased hit range at the cost of higher energy cost, cooldown and reduced knockback.",
                50000,
                () -> {
                    ability.setPveUpgrade(true);
                    ability.setBoulderSpeed(ability.getBoulderSpeed() * 0.25f);
                    ability.setCooldown(ability.getCooldown() * 2);
                    ability.setEnergyCost(ability.getEnergyCost() * 1.5f);
                    ability.setMinDamageHeal(ability.getMinDamageHeal() * 4);
                    ability.setMaxDamageHeal(ability.getMaxDamageHeal() * 4);
                    ability.setHitbox(hitbox + 3);
                }
        );
    }
}
