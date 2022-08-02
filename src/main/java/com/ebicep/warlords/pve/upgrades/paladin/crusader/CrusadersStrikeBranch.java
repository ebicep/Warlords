package com.ebicep.warlords.pve.upgrades.paladin.crusader;

import com.ebicep.warlords.abilties.CrusadersStrike;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class CrusadersStrikeBranch extends AbstractUpgradeBranch<CrusadersStrike> {

    public CrusadersStrikeBranch(AbilityTree abilityTree, CrusadersStrike ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Damage - Tier I", "+15% Damage", 5000));
        treeA.add(new Upgrade("Damage - Tier II", "+30% Damage", 10000));
        treeA.add(new Upgrade("Damage - Tier III", "+60% Damage", 20000));

        treeB.add(new Upgrade("Energy - Tier I", "-5 Energy cost", 5000));
        treeB.add(new Upgrade("Energy - Tier II", "-10 Energy cost", 10000));
        treeB.add(new Upgrade("Energy - Tier III", "-15 Energy cost", 20000));

        treeC.add(new Upgrade("Ally Energy - Tier I", "+2 Energy given", 5000));
        treeC.add(new Upgrade("Ally Energy - Tier II", "+4 Energy given", 10000));
        treeC.add(new Upgrade("Ally Energy - Tier III", "+6 Energy given", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "+100% Energy given range\n\nCrusader's Strike hits 2 additional targets\nand provides energy up to 2 extra allies.",
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

    int energyGiven = ability.getEnergyGiven();

    @Override
    public void c1() {
        ability.setEnergyGiven(energyGiven + 2);
    }

    @Override
    public void c2() {
        ability.setEnergyGiven(energyGiven + 4);
    }

    @Override
    public void c3() {
        ability.setEnergyGiven(energyGiven + 6);
    }

    @Override
    public void c4() {

    }

    @Override
    public void master() {
        ability.setEnergyRadius(ability.getEnergyRadius() * 2);
        ability.setEnergyMaxAllies(ability.getEnergyMaxAllies() + 2);
        ability.setPveUpgrade(true);
    }
}
