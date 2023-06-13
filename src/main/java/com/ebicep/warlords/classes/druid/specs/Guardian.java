package com.ebicep.warlords.classes.druid.specs;

import com.ebicep.warlords.abilties.EnergySeerGuardian;
import com.ebicep.warlords.abilties.FortifyingHex;
import com.ebicep.warlords.abilties.Temp;
import com.ebicep.warlords.classes.druid.AbstractDruid;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;

import java.util.List;

public class Guardian extends AbstractDruid {

    public Guardian() {
        super(
                "Guardian",
                5400,
                305,
                20,
                14,
                0,
                new FortifyingHex(),
                new Temp(),
                new EnergySeerGuardian(),
                new Temp(),
                new Temp()
        );
    }

    @Override
    public void setUpgradeBranches(WarlordsPlayer wp) {
        AbilityTree abilityTree = wp.getAbilityTree();
        List<AbstractUpgradeBranch<?>> branch = abilityTree.getUpgradeBranches();

    }
}
