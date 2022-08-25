package com.ebicep.warlords.pve.upgrades.rogue.vindicator;

import com.ebicep.warlords.abilties.PrismGuard;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class PrismGuardBranch extends AbstractUpgradeBranch<PrismGuard> {

    public PrismGuardBranch(AbilityTree abilityTree, PrismGuard ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Damage Reduction - Tier I", "+5% Damage reduction", 5000));
        treeA.add(new Upgrade("Damage Reduction - Tier II", "+10% Damage reduction", 10000));
        treeA.add(new Upgrade("Damage Reduction - Tier III", "+20% Damage reduction", 20000));

        treeC.add(new Upgrade("Healing - Tier I", "+10% Healing", 5000));
        treeC.add(new Upgrade("Healing - Tier II", "+25% Healing", 10000));
        treeC.add(new Upgrade("Healing - Tier III", "+50% Healing", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "Double the range of Prism Guard, nullify any damage\nthat incoming projectiles would've dealt.",
                50000
        );
    }

    int damageReduction = ability.getDamageReduction();
    int projectileDamageReduction = ability.getProjectileDamageReduction();
    int bubbleHealing = ability.getBubbleHealing();
    float bubbleMissingHealing = ability.getBubbleMissingHealing();
}
