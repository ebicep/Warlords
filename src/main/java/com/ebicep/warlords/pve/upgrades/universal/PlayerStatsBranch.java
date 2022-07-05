package com.ebicep.warlords.pve.upgrades.universal;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;

public class PlayerStatsBranch extends AbstractUpgradeBranch<AbstractAbility> {

    public PlayerStatsBranch(AbilityTree abilityTree, AbstractAbility ability) {
        super(abilityTree, ability);
    }

    int health = abilityTree.getPlayer().getMaxHealth();

    @Override
    public void a1() {
        abilityTree.getPlayer().setMaxHealth((int) (health * 1.1f));
    }

    @Override
    public void a2() {

    }

    @Override
    public void a3() {

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
