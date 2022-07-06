package com.ebicep.warlords.pve.upgrades.paladin.avenger;

import com.ebicep.warlords.abilties.AvengersStrike;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class AvengerStrikeBranch extends AbstractUpgradeBranch<AvengersStrike> {

    public AvengerStrikeBranch(AbilityTree abilityTree, AvengersStrike ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Damage - Tier I", "+15% Damage", 5000));
        treeA.add(new Upgrade("Damage - Tier II", "+30% Damage", 10000));
        treeA.add(new Upgrade("Damage - Tier III", "+60% Damage", 20000));

        treeB.add(new Upgrade("Energy - Tier I", "-5 Energy cost", 5000));
        treeB.add(new Upgrade("Energy - Tier II", "-10 Energy cost", 10000));
        treeB.add(new Upgrade("Energy - Tier III", "-15 Energy cost", 20000));

        treeC.add(new Upgrade("Drain - Tier I", "+5 Energy drain", 5000));
        treeC.add(new Upgrade("Drain - Tier II", "+10 Energy drain", 10000));
        treeC.add(new Upgrade("Drain - Tier III", "+20 Energy drain", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "Avenger's Strike hits 2 additional targets.",
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

    int energySteal = ability.getEnergySteal();
    @Override
    public void c1() {
        ability.setEnergySteal(energySteal + 5);
    }

    @Override
    public void c2() {
        ability.setEnergySteal(energySteal + 10);
    }

    @Override
    public void c3() {
        ability.setEnergySteal(energySteal + 20);
    }

    @Override
    public void master() {
        ability.setPveUpgrade(true);
    }
}
