package com.ebicep.warlords.pve.upgrades.rogue.assassin;

import com.ebicep.warlords.abilties.JudgementStrike;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class JudgementStrikeBranch extends AbstractUpgradeBranch<JudgementStrike> {

    public JudgementStrikeBranch(AbilityTree abilityTree, JudgementStrike ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Damage - Tier I", "+25% Damage", 5000));
        treeA.add(new Upgrade("Damage - Tier II", "+50% Damage", 10000));
        treeA.add(new Upgrade("Damage - Tier III", "+100% Damage", 20000));

        treeB.add(new Upgrade("Energy - Tier I", "-5 Energy cost", 5000));
        treeB.add(new Upgrade("Energy - Tier II", "-10 Energy cost", 10000));
        treeB.add(new Upgrade("Energy - Tier III", "-15 Energy cost", 20000));

        treeC.add(new Upgrade("Speed - Tier I", "+5% Speed on crit", 5000));
        treeC.add(new Upgrade("Speed - Tier II", "+10% Speed on crit", 10000));
        treeC.add(new Upgrade("Speed - Tier III", "+20% Speed on crit", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "Every third Judgement Strike is now a guaranteed\ncritical hit. Movement speed buff on critical hit\nnow lasts 4 seconds.",
                50000
        );
    }

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();

    int energyCost = ability.getEnergyCost();

    @Override
    public void b1() {
        ability.setEnergyCost(energyCost - 5);
    }

    @Override
    public void b2() {
        ability.setEnergyCost(energyCost - 10);
    }

    @Override
    public void b3() {
        ability.setEnergyCost(energyCost - 15);
    }

    @Override
    public void b4() {

    }

    int speedOnCrit = ability.getSpeedOnCrit();

    @Override
    public void c1() {
        ability.setSpeedOnCrit(speedOnCrit + 5);
    }

    @Override
    public void c2() {
        ability.setSpeedOnCrit(speedOnCrit + 10);
    }

    @Override
    public void c3() {
        ability.setSpeedOnCrit(speedOnCrit + 20);
    }

    @Override
    public void c4() {

    }

    @Override
    public void master() {
        ability.setStrikeCritInterval(ability.getStrikeCritInterval() - 1);
        ability.setSpeedOnCritDuration(ability.getSpeedOnCritDuration() + 2);
    }
}
