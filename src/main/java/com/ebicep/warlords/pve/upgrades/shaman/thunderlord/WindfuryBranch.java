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
                "+15% Weapon damage\n-5% Cooldown reduction",
                5000,
                () -> {
                    ability.setWeaponDamage(weaponDamage + 15);
                    ability.setCooldown(cooldown * 0.95f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "+30% Weapon damage\n-10% Cooldown reduction",
                10000,
                () -> {
                    ability.setWeaponDamage(weaponDamage + 30);
                    ability.setCooldown(cooldown * 0.9f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "+45% Weapon damage\n-15% Cooldown reduction",
                15000,
                () -> {
                    ability.setWeaponDamage(weaponDamage + 45);
                    ability.setCooldown(cooldown * 0.85f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "+60% Weapon Damage\n-20% Cooldown reduction",
                20000,
                () -> {
                    ability.setWeaponDamage(weaponDamage + 60);
                    ability.setCooldown(cooldown * 0.8f);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "+4% Proc chance\n",
                5000,
                () -> {
                    ability.setProcChance(procChance + 4);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "+8% Proc chance\n+1 Windfury hit",
                10000,
                () -> {
                    ability.setProcChance(procChance + 8);
                    ability.setMaxHits(maxHits + 1);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "+12% Proc chance",
                15000,
                () -> {
                    ability.setProcChance(procChance + 12);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "+16% Proc chance\n+2 Windfury hits",
                20000,
                () -> {
                    ability.setProcChance(procChance + 16);
                    ability.setMaxHits(maxHits + 2);
                }
        ));

        masterUpgrade = new Upgrade(
                "Shredding Fury",
                "Windfury - Master Upgrade",
                "+100% Additional weapon damage\n\nHits on an enemy will permanently reduce their\ndamage reduction by 2% for each Windfury additional\nproc.",
                50000,
                () -> {
                    ability.setPveUpgrade(true);
                    ability.setWeaponDamage(ability.getWeaponDamage() + 100);
                }
        );
    }

}
