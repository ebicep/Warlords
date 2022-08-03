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

    int energyCost = ability.getEnergyCost();

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
    public void c4() {

    }

    @Override
    public void master() {
        ability.setPveUpgrade(true);
    }
}
