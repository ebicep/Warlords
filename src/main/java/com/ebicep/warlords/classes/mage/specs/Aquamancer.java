package com.ebicep.warlords.classes.mage.specs;

import com.ebicep.warlords.abilties.*;
import com.ebicep.warlords.classes.mage.AbstractMage;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.UpgradeBranch;

import java.util.List;

public class Aquamancer extends AbstractMage {

    public Aquamancer() {
        super(
                "Aquamancer",
                5200,
                355,
                20,
                14,
                0,
                new WaterBolt(),
                new WaterBreath(),
                new TimeWarp(),
                new ArcaneShield(),
                new HealingRain()
        );
    }

    @Override
    public void setUpgradeBranches(WarlordsPlayer wp) {
        AbilityTree abilityTree = wp.getAbilityTree();
        List<UpgradeBranch<?>> branch = abilityTree.getUpgradeBranches();
    }
}
