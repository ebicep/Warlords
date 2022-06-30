package com.ebicep.warlords.classes.shaman.specs;

import com.ebicep.warlords.abilties.*;
import com.ebicep.warlords.classes.shaman.AbstractShaman;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.UpgradeBranch;
import com.ebicep.warlords.pve.upgrades.thunderlord.ChainLightningBranch;
import com.ebicep.warlords.pve.upgrades.thunderlord.LightningBoltBranch;

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
        List<UpgradeBranch<?>> branch = abilityTree.getUpgradeBranches();
        branch.add(new LightningBoltBranch(abilityTree, (LightningBolt) weapon, wp.getItemStackForAbility(weapon), weapon.getName()));
        branch.add(new ChainLightningBranch(abilityTree, (ChainLightning) red, wp.getItemStackForAbility(red), red.getName()));
    }
}
