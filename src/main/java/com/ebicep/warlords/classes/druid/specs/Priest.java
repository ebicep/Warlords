package com.ebicep.warlords.classes.druid.specs;

import com.ebicep.warlords.abilties.*;
import com.ebicep.warlords.classes.druid.AbstractDruid;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;

import java.util.List;

public class Priest extends AbstractDruid {

    public Priest() {
        super(
                "Priest",
                5750,
                355,
                20,
                14,
                0,
                new MercifulHex(),
                new BeaconOfLight(),
                new EnergySeerPriest(),
                new BeaconOfImpair(),
                new DivineBlessing()
        );
    }

    @Override
    public void setUpgradeBranches(WarlordsPlayer wp) {
        AbilityTree abilityTree = wp.getAbilityTree();
        List<AbstractUpgradeBranch<?>> branch = abilityTree.getUpgradeBranches();

    }
}
