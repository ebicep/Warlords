package com.ebicep.warlords.classes.mage.specs;

import com.ebicep.warlords.abilties.*;
import com.ebicep.warlords.classes.mage.AbstractMage;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.mage.cryomancer.FreezingBreathBranch;
import com.ebicep.warlords.pve.upgrades.mage.cryomancer.FrostboltBranch;
import com.ebicep.warlords.pve.upgrades.mage.cryomancer.IceBarrierBranch;
import com.ebicep.warlords.pve.upgrades.mage.pyromancer.ArcaneShieldBranch;
import com.ebicep.warlords.pve.upgrades.mage.pyromancer.TimeWarpBranch;

import java.util.List;

public class Cryomancer extends AbstractMage {

    public Cryomancer() {
        super(
                "Cryomancer",
                6135,
                305,
                20,
                14,
                10,
                new FrostBolt(),
                new FreezingBreath(),
                new TimeWarp(),
                new ArcaneShield(),
                new IceBarrier()
        );
    }

    @Override
    public void setUpgradeBranches(WarlordsPlayer wp) {
        AbilityTree abilityTree = wp.getAbilityTree();
        List<AbstractUpgradeBranch<?>> branch = abilityTree.getUpgradeBranches();
        branch.add(new FrostboltBranch(abilityTree, (FrostBolt) weapon));
        branch.add(new FreezingBreathBranch(abilityTree, (FreezingBreath) red));
        branch.add(new TimeWarpBranch(abilityTree, (TimeWarp) purple));
        branch.add(new ArcaneShieldBranch(abilityTree, (ArcaneShield) blue));
        branch.add(new IceBarrierBranch(abilityTree, (IceBarrier) orange));
    }
}
