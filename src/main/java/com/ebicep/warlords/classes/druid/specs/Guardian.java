package com.ebicep.warlords.classes.druid.specs;

import com.ebicep.warlords.abilties.*;
import com.ebicep.warlords.classes.druid.AbstractDruid;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;

import java.util.List;

public class Guardian extends AbstractDruid {

    public Guardian() {
        super(
                "Guardian",
                6000,
                305,
                20,
                14,
                10,
                new FortifyingHex(),
                new NotAShield(),
                new EnergySeerGuardian(),
                new SpiritualShield(),
                new Sanctuary()
        );
    }

    @Override
    public void setUpgradeBranches(WarlordsPlayer wp) {
        AbilityTree abilityTree = wp.getAbilityTree();
        List<AbstractUpgradeBranch<?>> branch = abilityTree.getUpgradeBranches();

    }
}
