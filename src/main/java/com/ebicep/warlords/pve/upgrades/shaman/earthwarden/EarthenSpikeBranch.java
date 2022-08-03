package com.ebicep.warlords.pve.upgrades.shaman.earthwarden;

import com.ebicep.warlords.abilties.EarthenSpike;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class EarthenSpikeBranch extends AbstractUpgradeBranch<EarthenSpike> {
    public EarthenSpikeBranch(AbilityTree abilityTree, EarthenSpike ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Damage - Tier I", "+10% Damage", 5000));
        treeA.add(new Upgrade("Damage - Tier II", "+20% Damage", 10000));
        treeA.add(new Upgrade("Damage - Tier III", "+40% Damage", 20000));

        treeB.add(new Upgrade("Energy - Tier I", "-10 Energy cost", 5000));
        treeB.add(new Upgrade("Energy - Tier II", "-20 Energy cost", 10000));
        treeB.add(new Upgrade("Energy - Tier III", "-30 Energy cost", 20000));

        treeC.add(new Upgrade("Speed - Tier I", "+20% Spike Speed", 5000));
        treeC.add(new Upgrade("Speed - Tier II", "+40% Spike Speed", 10000));
        treeC.add(new Upgrade("Speed - Tier III", "+80% Spike Speed", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "+50% Splash hit radius\n\nEarthen Spike deals 50% more vertical\nknockback and applies the FLOAT status.\n\nFLOAT: All knockback taken is increased by\n50% for 10 seconds.",
                50000
        );
    }

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();

    float energyCost = ability.getEnergyCost();

    float speed = ability.getSpeed();

}
