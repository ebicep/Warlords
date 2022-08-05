package com.ebicep.warlords.pve.upgrades.mage;

import com.ebicep.warlords.abilties.ArcaneShield;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class ArcaneShieldBranch extends AbstractUpgradeBranch<ArcaneShield> {

    public ArcaneShieldBranch(AbilityTree abilityTree, ArcaneShield ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Cooldown - Tier I", "-10% Cooldown reduction", 5000));
        treeA.add(new Upgrade("Cooldown - Tier II", "-20% Cooldown reduction", 10000));
        treeA.add(new Upgrade("Cooldown - Tier III", "-40% Cooldown reduction", 20000));

        treeC.add(new Upgrade("Absorption - Tier I", "+10% Max shield health", 5000));
        treeC.add(new Upgrade("Absorption - Tier II", "+25% Max shield health", 10000));
        treeC.add(new Upgrade("Absorption - Tier III", "+50% Max shield health", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "PLACEHOLDER: +100% Duration",
                50000
        );
    }

    float cooldown = ability.getCooldown();

}
