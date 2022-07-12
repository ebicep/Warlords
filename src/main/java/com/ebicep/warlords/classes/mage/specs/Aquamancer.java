package com.ebicep.warlords.classes.mage.specs;

import com.ebicep.warlords.abilties.*;
import com.ebicep.warlords.classes.mage.AbstractMage;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.mage.ArcaneShieldBranch;
import com.ebicep.warlords.pve.upgrades.mage.TimeWarpBranch;
import com.ebicep.warlords.pve.upgrades.mage.aquamancer.HealingRainBranch;
import com.ebicep.warlords.pve.upgrades.mage.aquamancer.WaterBoltBranch;
import com.ebicep.warlords.pve.upgrades.mage.aquamancer.WaterBreathBranch;

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
        List<AbstractUpgradeBranch<?>> branch = abilityTree.getUpgradeBranches();
        branch.add(new WaterBoltBranch(abilityTree, (WaterBolt) weapon));
        branch.add(new WaterBreathBranch(abilityTree, (WaterBreath) red));
        branch.add(new TimeWarpBranch(abilityTree, (TimeWarp) purple));
        branch.add(new ArcaneShieldBranch(abilityTree, (ArcaneShield) blue));
        branch.add(new HealingRainBranch(abilityTree, (HealingRain) orange));
    }
}
