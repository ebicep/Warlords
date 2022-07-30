package com.ebicep.warlords.pve.upgrades.shaman.thunderlord;

import com.ebicep.warlords.abilties.Windfury;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class WindfuryBranch extends AbstractUpgradeBranch<Windfury> {

    public WindfuryBranch(AbilityTree abilityTree, Windfury ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Weapon Damage - Tier I", "+20% Weapon Damage", 5000));
        treeA.add(new Upgrade("Weapon Damage - Tier II", "+40% Weapon Damage", 10000));
        treeA.add(new Upgrade("Weapon Damage - Tier III", "+80% Weapon Damage", 20000));

        treeC.add(new Upgrade("Proc Chance - Tier I", "+5% Proc chance", 5000));
        treeC.add(new Upgrade("Proc Chance - Tier II", "+10% Proc chance", 10000));
        treeC.add(new Upgrade("Proc Chance - Tier III", "+20% Proc chance", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "Windfury now procs an additional 2 times.",
                50000
        );
    }

    float weaponDamage = ability.getWeaponDamage();

    @Override
    public void a1() {
        ability.setWeaponDamage(weaponDamage + 20);
    }

    @Override
    public void a2() {
        ability.setWeaponDamage(weaponDamage + 40);
    }

    @Override
    public void a3() {
        ability.setWeaponDamage(weaponDamage + 80);
    }

    @Override
    public void a4() {

    }

    @Override
    public void b1() {

    }

    @Override
    public void b2() {

    }

    @Override
    public void b3() {

    }

    @Override
    public void b4() {

    }

    @Override
    public void c1() {
        ability.setProcChance(ability.getProcChance() + 5);
    }

    @Override
    public void c2() {
        ability.setProcChance(ability.getProcChance() + 5);
    }

    @Override
    public void c3() {
        ability.setProcChance(ability.getProcChance() + 10);
    }

    @Override
    public void c4() {

    }

    @Override
    public void master() {
        ability.setMaxHits(ability.getMaxHits() + 2);
    }
}
