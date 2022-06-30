package com.ebicep.warlords.classes.shaman.specs;

import com.ebicep.warlords.abilties.*;
import com.ebicep.warlords.classes.shaman.AbstractShaman;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.thunderlord.ChainLightningBranch;

import java.util.List;

public class Thunderlord extends AbstractShaman {

    public Thunderlord() {
        super(
                "Thunderlord",
                5200,
                305,
                0,
                new LightningBolt(),
                new ChainLightning(),
                new Windfury(),
                new LightningRod(),
                new CapacitorTotem()
        );
    }

    @Override
    public void setUpgradeBranches(WarlordsPlayer wp) {
        AbilityTree abilityTree = wp.getAbilityTree();
        List<AbstractUpgradeBranch<?>> branch = abilityTree.getUpgradeBranches();
        branch.add(new ChainLightningBranch(abilityTree, (ChainLightning) red));
    }
}
