package com.ebicep.warlords.pve.upgrades.rogue.apothecary;

import com.ebicep.warlords.abilties.DrainingMiasma;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class DrainingMiasmaBranch extends AbstractUpgradeBranch<DrainingMiasma> {

    public DrainingMiasmaBranch(AbilityTree abilityTree, DrainingMiasma ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Duration - Tier I", "+1s Duration", 5000));
        treeA.add(new Upgrade("Duration - Tier II", "+2s Duration", 10000));
        treeA.add(new Upgrade("Duration - Tier III", "+3s Duration", 20000));

        treeC.add(new Upgrade("Range - Tier I", "+2 Blocks radius", 5000));
        treeC.add(new Upgrade("Range - Tier II", "+4 Blocks radius", 10000));
        treeC.add(new Upgrade("Range - Tier III", "+6 Blocks radius", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "For each enemy within Draining Miasma,\ngain a 4% damage boost for Impaling Strike for\nthe duration of Draining Miasma",
                50000
        );
    }

    int duration = ability.getDuration();

    int hitRadius = ability.getEnemyHitRadius();

}
