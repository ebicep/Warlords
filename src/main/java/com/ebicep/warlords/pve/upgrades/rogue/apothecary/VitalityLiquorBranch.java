package com.ebicep.warlords.pve.upgrades.rogue.apothecary;

import com.ebicep.warlords.abilties.VitalityLiquor;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class VitalityLiquorBranch extends AbstractUpgradeBranch<VitalityLiquor> {

    public VitalityLiquorBranch(AbilityTree abilityTree, VitalityLiquor ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Range - Tier I", "+2 Blocks radius", 5000));
        treeA.add(new Upgrade("Range - Tier II", "+4 Blocks radius", 10000));
        treeA.add(new Upgrade("Range - Tier III", "+6 Blocks radius", 20000));

        treeC.add(new Upgrade("Cooldown - Tier I", "-10% Cooldown Reduction", 5000));
        treeC.add(new Upgrade("Cooldown - Tier II", "-20% Cooldown Reduction", 10000));
        treeC.add(new Upgrade("Cooldown - Tier III", "-40% Cooldown Reduction", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "PLACEHOLDER: +100% Healing",
                50000
        );
    }

    int vitalityRange = ability.getVitalityRange();

    float cooldown = ability.getCooldown();

}
