package com.ebicep.warlords.pve.upgrades.rogue.assassin;

import com.ebicep.warlords.abilties.OrderOfEviscerate;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class OrderOfEviscerateBranch extends AbstractUpgradeBranch<OrderOfEviscerate> {

    public OrderOfEviscerateBranch(AbilityTree abilityTree, OrderOfEviscerate ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Mark Damage - Tier I", "+20% Damage against marked targets", 5000));
        treeA.add(new Upgrade("Mark Damage - Tier II", "+40% Damage against marked targets", 10000));
        treeA.add(new Upgrade("Mark Damage - Tier III", "+80% Damage against marked targets", 20000));

        treeC.add(new Upgrade("Duration - Tier I", "+2s Duration", 5000));
        treeC.add(new Upgrade("Duration - Tier II", "+4s Duration", 10000));
        treeC.add(new Upgrade("Duration - Tier III", "+8s Duration", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "Gain 0.2% Crit chance and Crit Multiplier for\neach instance of damage you deal to an enemy\nwhile Berserk is active. (Max 30%)",
                50000
        );
    }

    // WIP

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

    }

    @Override
    public void c2() {

    }

    @Override
    public void c3() {

    }

    @Override
    public void c4() {

    }

    @Override
    public void master() {

    }
}
