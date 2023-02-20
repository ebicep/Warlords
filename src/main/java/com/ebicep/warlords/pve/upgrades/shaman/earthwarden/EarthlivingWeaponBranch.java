package com.ebicep.warlords.pve.upgrades.shaman.earthwarden;

import com.ebicep.warlords.abilties.Earthliving;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class EarthlivingWeaponBranch extends AbstractUpgradeBranch<Earthliving> {

    int weaponDamage = ability.getWeaponDamage();
    int maxHits = ability.getMaxHits();
    float cooldown = ability.getCooldown();

    public EarthlivingWeaponBranch(AbilityTree abilityTree, Earthliving ability) {
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
                "+2% Proc chance",
                5000,
                () -> {
                    ability.setProcChance(ability.getProcChance() + 2);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "+4% Proc chance",
                10000,
                () -> {
                    ability.setProcChance(ability.getProcChance() + 2);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "+6% Proc chance",
                15000,
                () -> {
                    ability.setProcChance(ability.getProcChance() + 2);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "+8% Proc chance\n+1 Earthliving Weapon hit",
                20000,
                () -> {
                    ability.setProcChance(ability.getProcChance() + 2);
                    ability.setMaxHits(maxHits + 1);
                }
        ));

        masterUpgrade = new Upgrade(
                "Loamliving Weapon",
                "Earthliving Weapon - Master Upgrade",
                "Each additional Earthliving Weapon proc on an enemy stuns them for 2 seconds. After 2 seconds they will emit a shockwave that heals all " +
                        "nearby allies for 10% of their missing health and restore energy equal to the same amount.",
                50000,
                () -> {
                    ability.setPveUpgrade(true);
                }
        );
    }
}
