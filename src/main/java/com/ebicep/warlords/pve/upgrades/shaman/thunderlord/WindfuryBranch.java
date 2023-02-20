package com.ebicep.warlords.pve.upgrades.shaman.thunderlord;

import com.ebicep.warlords.abilties.Windfury;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class WindfuryBranch extends AbstractUpgradeBranch<Windfury> {

    float weaponDamage = ability.getWeaponDamage();
    float cooldown = ability.getCooldown();
    int maxHits = ability.getMaxHits();

    public WindfuryBranch(AbilityTree abilityTree, Windfury ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade(
                "Impair - Tier I",
                "+25% Weapon damage\n-5% Cooldown reduction",
                5000,
                () -> {
                    ability.setWeaponDamage(weaponDamage + 25);
                    ability.setCooldown(cooldown * 0.95f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "+50% Weapon damage\n-10% Cooldown reduction",
                10000,
                () -> {
                    ability.setWeaponDamage(weaponDamage + 50);
                    ability.setCooldown(cooldown * 0.9f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "+75% Weapon damage\n-15% Cooldown reduction",
                15000,
                () -> {
                    ability.setWeaponDamage(weaponDamage + 75);
                    ability.setCooldown(cooldown * 0.85f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "+100% Weapon Damage\n-20% Cooldown reduction",
                20000,
                () -> {
                    ability.setWeaponDamage(weaponDamage + 100);
                    ability.setCooldown(cooldown * 0.8f);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "+4% Proc chance",
                5000,
                () -> {
                    ability.setProcChance(ability.getProcChance() + 4);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "+8% Proc chance",
                10000,
                () -> {
                    ability.setProcChance(ability.getProcChance() + 4);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "+12% Proc chance",
                15000,
                () -> {
                    ability.setProcChance(ability.getProcChance() + 4);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "+16% Proc chance\n+1 Windfury hit",
                20000,
                () -> {
                    ability.setProcChance(ability.getProcChance() + 4);
                    ability.setMaxHits(maxHits + 1);
                }
        ));

        masterUpgrade = new Upgrade(
                "Shredding Fury",
                "Windfury - Master Upgrade",
                "Each hit deals 1% of the target's max health as bonus damage.\n\nHits on an enemy will permanently reduce their damage reduction by 2% for each " +
                        "additional Windfury proc.",
                50000,
                () -> {
                    ability.setPveUpgrade(true);
                }
        );
    }

}
