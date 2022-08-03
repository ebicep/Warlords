package com.ebicep.warlords.pve.upgrades.shaman.earthwarden;

import com.ebicep.warlords.abilties.Earthliving;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class EarthlivingWeaponBranch extends AbstractUpgradeBranch<Earthliving> {
    public EarthlivingWeaponBranch(AbilityTree abilityTree, Earthliving ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Weapon Damage - Tier I", "+15% Weapon Damage", 5000));
        treeA.add(new Upgrade("Weapon Damage - Tier II", "+30% Weapon Damage", 10000));
        treeA.add(new Upgrade("Weapon Damage - Tier III", "+60% Weapon Damage", 20000));

        treeC.add(new Upgrade("Proc Chance - Tier I", "+5% Proc Chance", 5000));
        treeC.add(new Upgrade("Proc Chance - Tier II", "+10% Proc Chance", 10000));
        treeC.add(new Upgrade("Proc Chance - Tier III", "+20% Proc Chance", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "Earthliving Weapon procs an additional 2 times.",
                50000
        );
    }

    int weaponDamage = ability.getWeaponDamage();

    int procChance = ability.getProcChance();

    @Override
    public void c1() {
        ability.setProcChance(procChance + 5);
    }

    @Override
    public void c2() {
        ability.setProcChance(procChance + 10);
    }

    @Override
    public void c3() {
        ability.setProcChance(procChance + 20);
    }

    @Override
    public void c4() {

    }

    @Override
    public void master() {
        ability.setMaxHits(ability.getMaxHits() + 2);
    }
}
