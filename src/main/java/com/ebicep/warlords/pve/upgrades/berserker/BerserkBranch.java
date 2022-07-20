package com.ebicep.warlords.pve.upgrades.berserker;

import com.ebicep.warlords.abilties.Berserk;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class BerserkBranch extends AbstractUpgradeBranch<Berserk> {

    public BerserkBranch(AbilityTree abilityTree, Berserk ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Damage Boost - Tier I", "+10% Damage boost", 5000));
        treeA.add(new Upgrade("Damage Boost - Tier II", "+15% Damage boost", 10000));
        treeA.add(new Upgrade("Damage Boost - Tier III", "+20% Damage boost", 20000));

        treeC.add(new Upgrade("Speed - Tier I", "+10% Speed", 5000));
        treeC.add(new Upgrade("Speed - Tier II", "+20% Speed", 10000));
        treeC.add(new Upgrade("Speed - Tier III", "+30% Speed", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "Gain 0.2% Crit chance and Crit Multiplier for\neach instance of damage you deal to an enemy\nwhile Berserk is active. (Max 30%)",
                50000
        );
    }

    float damageBoost = ability.getDamageIncrease();

    @Override
    public void a1() {
        ability.setDamageIncrease(damageBoost + 15);
    }

    @Override
    public void a2() {
        ability.setDamageIncrease(damageBoost + 30);
    }

    @Override
    public void a3() {
        ability.setDamageIncrease(damageBoost + 60);
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

    int speedBuff = ability.getSpeedBuff();

    @Override
    public void c1() {
        ability.setSpeedBuff(speedBuff + 10);
    }

    @Override
    public void c2() {
        ability.setSpeedBuff(speedBuff + 20);
    }

    @Override
    public void c3() {
        ability.setSpeedBuff(speedBuff + 30);
    }

    @Override
    public void util1() {

    }

    @Override
    public void util2() {

    }

    @Override
    public void master() {
        ability.setPveUpgrade(true);
    }
}
