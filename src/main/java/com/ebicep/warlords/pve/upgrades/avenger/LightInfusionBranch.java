package com.ebicep.warlords.pve.upgrades.avenger;

import com.ebicep.warlords.abilties.LightInfusion;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class LightInfusionBranch extends AbstractUpgradeBranch<LightInfusion> {

    public LightInfusionBranch(AbilityTree abilityTree, LightInfusion ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Speed - Tier I", "+20% Speed", 5000));
        treeA.add(new Upgrade("Speed - Tier II", "+40% Speed", 10000));
        treeA.add(new Upgrade("Speed - Tier III", "+80% Speed", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "Increase duration by 100%",
                500000
        );
    }

    @Override
    public void a1() {
        ability.setSpeedBuff(60);
    }

    @Override
    public void a2() {
        ability.setSpeedBuff(80);
    }

    @Override
    public void a3() {
        ability.setSpeedBuff(120);
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

    }

    @Override
    public void c2() {

    }

    @Override
    public void c3() {

    }

    @Override
    public void master() {

    }
}
