package com.ebicep.warlords.pve.upgrades.shaman.thunderlord;

import com.ebicep.warlords.abilties.Windfury;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class WindfuryBranch extends AbstractUpgradeBranch<Windfury> {

    public WindfuryBranch(AbilityTree abilityTree, Windfury ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Damage - Tier I", "+20% Damage", 5000));
        treeA.add(new Upgrade("Damage - Tier II", "+40% Damage", 10000));
        treeA.add(new Upgrade("Damage - Tier III", "+80% Damage", 20000));

        treeC.add(new Upgrade("Proc Chance - Tier I", "+5% Proc chance", 5000));
        treeC.add(new Upgrade("Proc Chance - Tier II", "+10% Proc chance", 10000));
        treeC.add(new Upgrade("Proc Chance - Tier III", "+20% Proc chance", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "Windfury now procs an additional 2 times.",
                50000
        );
    }

    @Override
    public void a1() {
        ability.setWeaponDamage(155);
    }

    @Override
    public void a2() {
        ability.setWeaponDamage(175);
    }

    @Override
    public void a3() {
        ability.setWeaponDamage(215);
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
    public void master() {
        ability.setMaxHits(ability.getMaxHits() + 2);
    }
}
