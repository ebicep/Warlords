package com.ebicep.warlords.pve.upgrades.rogue.apothecary;

import com.ebicep.warlords.abilties.RemedicChains;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class RemedicChainsBranch extends AbstractUpgradeBranch<RemedicChains> {

    public RemedicChainsBranch(AbilityTree abilityTree, RemedicChains ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Healing - Tier I", "+10% Healing", 5000));
        treeA.add(new Upgrade("Healing - Tier II", "+20% Healing", 10000));
        treeA.add(new Upgrade("Healing - Tier III", "+40% Healing", 20000));

        treeC.add(new Upgrade("Cooldown - Tier I", "-10% Cooldown Reduction", 5000));
        treeC.add(new Upgrade("Cooldown - Tier II", "-20% Cooldown Reduction", 10000));
        treeC.add(new Upgrade("Cooldown - Tier III", "-40% Cooldown Reduction", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "+10 Blocks cast and break range.\n\nIncrease the damage boost provided by\nRemedic Chains by 30%",
                50000
        );
    }

    float minHealing = ability.getMinDamageHeal();
    float maxHealing = ability.getMaxDamageHeal();

    float cooldown = ability.getCooldown();

}
