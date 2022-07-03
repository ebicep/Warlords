package com.ebicep.warlords.pve.upgrades.assassin;

import com.ebicep.warlords.abilties.JudgementStrike;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class JudgementStrikeBranch extends AbstractUpgradeBranch<JudgementStrike> {

    public JudgementStrikeBranch(AbilityTree abilityTree, JudgementStrike ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Damage - Tier I", "+15% Damage", 5000));
        treeA.add(new Upgrade("Damage - Tier II", "+30% Damage", 10000));
        treeA.add(new Upgrade("Damage - Tier III", "+60% Damage", 20000));

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

    @Override
    public void a1() {
        ability.setMinDamageHeal(minDamage * 1.15f);
        ability.setMaxDamageHeal(maxDamage * 1.15f);
    }

    @Override
    public void a2() {
        ability.setMinDamageHeal(minDamage * 1.3f);
        ability.setMaxDamageHeal(maxDamage * 1.3f);
    }

    @Override
    public void a3() {
        ability.setMinDamageHeal(minDamage * 1.6f);
        ability.setMaxDamageHeal(maxDamage * 1.6f);
    }

    @Override
    public void b1() {
        ability.setEnergyCost(ability.getEnergyCost() - 5);
    }

    @Override
    public void b2() {
        ability.setEnergyCost(ability.getEnergyCost() - 5);
    }

    @Override
    public void b3() {
        ability.setEnergyCost(ability.getEnergyCost() - 5);
    }

    @Override
    public void c1() {
        ability.setSpeedOnCrit(ability.getSpeedOnCrit() + 5);
    }

    @Override
    public void c2() {
        ability.setSpeedOnCrit(ability.getSpeedOnCrit() + 5);
    }

    @Override
    public void c3() {
        ability.setSpeedOnCrit(ability.getSpeedOnCrit() + 10);
    }

    @Override
    public void master() {
        ability.setStrikeCritInterval(ability.getStrikeCritInterval() - 1);
        ability.setSpeedOnCritDuration(ability.getSpeedOnCritDuration() + 2);
    }
}
