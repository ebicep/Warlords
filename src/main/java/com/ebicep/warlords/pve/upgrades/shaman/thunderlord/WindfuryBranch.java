package com.ebicep.warlords.pve.upgrades.shaman.thunderlord;

import com.ebicep.warlords.abilties.Windfury;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class WindfuryBranch extends AbstractUpgradeBranch<Windfury> {

    float weaponDamage = ability.getWeaponDamage();
    float cooldown = ability.getCooldown();
    int procChance = ability.getProcChance();
    int maxHits = ability.getMaxHits();

    public WindfuryBranch(AbilityTree abilityTree, Windfury ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade(
                "Impair - Tier I",
                "+10% Weapon Damage\n-5% Cooldown Reduction",
                5000,
                () -> {
                    ability.setWeaponDamage(weaponDamage + 10);
                    ability.setCooldown(cooldown * 0.95f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "+20% Weapon Damage\n-10% Cooldown Reduction",
                10000,
                () -> {
                    ability.setWeaponDamage(weaponDamage + 20);
                    ability.setCooldown(cooldown * 0.9f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "+30% Weapon Damage\n-15% Cooldown Reduction",
                15000,
                () -> {
                    ability.setWeaponDamage(weaponDamage + 30);
                    ability.setCooldown(cooldown * 0.85f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "+40% Weapon Damage\n-20% Cooldown Reduction",
                20000,
                () -> {
                    ability.setWeaponDamage(weaponDamage + 40);
                    ability.setCooldown(cooldown * 0.8f);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "+5% Proc chance\n",
                5000,
                () -> {
                    ability.setProcChance(procChance + 5);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "+10% Proc chance\n+1 Windfury hit",
                10000,
                () -> {
                    ability.setProcChance(procChance + 10);
                    ability.setMaxHits(maxHits + 1);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "+15% Proc chance",
                15000,
                () -> {
                    ability.setProcChance(procChance + 15);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "+20% Proc chance\n+2 Windfury hits",
                20000,
                () -> {
                    ability.setProcChance(procChance + 20);
                    ability.setMaxHits(maxHits + 2);
                }
        ));

        masterUpgrade = new Upgrade(
                "Shredding Fury",
                "Windfury - Master Upgrade",
                "Hits on an enemy will permanently reduce their\ndamage reduction by 1% for each Windfury additional\nproc.",
                50000,
                () -> {

                }
        );
    }

}
