package com.ebicep.warlords.pve.upgrades.pyromancer;

import com.ebicep.warlords.abilties.Inferno;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class InfernoBranch extends AbstractUpgradeBranch<Inferno> {

    public InfernoBranch(AbilityTree abilityTree, Inferno ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Crit Chance - Tier I", "+5% Crit Chance bonus", 5000));
        treeA.add(new Upgrade("Crit Chance - Tier II", "+10% Crit Chance bonus", 10000));
        treeA.add(new Upgrade("Crit Chance - Tier III", "+20% Crit Chance bonus", 20000));

        treeC.add(new Upgrade("Crit Multiplier - Tier I", "+10% Crit Multiplier bonus", 5000));
        treeC.add(new Upgrade("Crit Multiplier - Tier II", "+20% Crit Multiplier bonus", 10000));
        treeC.add(new Upgrade("Crit Multiplier - Tier III", "+40% Crit Multiplier bonus", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "Reduce the cooldown of Inferno by 1 second\nfor each enemy killed. (0.5s on assists.)",
                2400
        );
    }

    @Override
    public void a1() {
        ability.setCritChanceIncrease(35);
    }

    @Override
    public void a2() {
        ability.setCritChanceIncrease(40);
    }

    @Override
    public void a3() {
        ability.setCritChanceIncrease(50);
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
        ability.setCritMultiplierIncrease(40);
    }

    @Override
    public void c2() {
        ability.setCritMultiplierIncrease(50);
    }

    @Override
    public void c3() {
        ability.setCritMultiplierIncrease(70);
    }

    @Override
    public void master() {
        ability.setPveUpgrade(true);
    }
}
