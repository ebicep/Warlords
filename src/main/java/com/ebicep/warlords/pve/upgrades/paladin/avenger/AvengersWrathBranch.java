package com.ebicep.warlords.pve.upgrades.paladin.avenger;

import com.ebicep.warlords.abilties.AvengersWrath;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class AvengersWrathBranch extends AbstractUpgradeBranch<AvengersWrath> {

    public AvengersWrathBranch(AbilityTree abilityTree, AvengersWrath ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Energy - Tier I", "+10 Energy per second", 5000));
        treeA.add(new Upgrade("Energy - Tier II", "+20 Energy per second", 10000));
        treeA.add(new Upgrade("Energy - Tier III", "+30 Energy per second", 20000));

        treeC.add(new Upgrade("Cooldown - Tier I", "-10% Cooldown reduction", 5000));
        treeC.add(new Upgrade("Cooldown - Tier II", "-20% Cooldown reduction", 10000));
        treeC.add(new Upgrade("Cooldown - Tier III", "-40% Cooldown reduction", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "-60% Cooldown reduction\n\nAvenger's Strike now hits 2 additional enemies and\ngain 2 energy per Avenger's Strike succesfully landed\non an enemy.",
                50000
        );
    }

    int energyCost = ability.getEnergyCost();

    float cooldown = ability.getCooldown();

}
