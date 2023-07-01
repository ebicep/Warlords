package com.ebicep.warlords.classes.mage.specs;

import com.ebicep.warlords.abilities.*;
import com.ebicep.warlords.classes.mage.AbstractMage;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.mage.ArcaneShieldBranch;
import com.ebicep.warlords.pve.upgrades.mage.pyromancer.FireballBranch;
import com.ebicep.warlords.pve.upgrades.mage.pyromancer.FlameburstBranch;
import com.ebicep.warlords.pve.upgrades.mage.pyromancer.InfernoBranch;
import com.ebicep.warlords.pve.upgrades.mage.pyromancer.TimeWarpBranchPyromancer;

import java.util.List;

public class Pyromancer extends AbstractMage {

    public Pyromancer() {
        super(
                "Pyromancer",
                5200,
                305,
                20,
                14,
                0,
                new Fireball(),
                new FlameBurst(),
                new TimeWarpPyromancer(),
                new ArcaneShield(),
                new Inferno()
        );
    }

    @Override
    public void setUpgradeBranches(WarlordsPlayer wp) {
        AbilityTree abilityTree = wp.getAbilityTree();
        List<AbstractUpgradeBranch<?>> branch = abilityTree.getUpgradeBranches();
        branch.add(new FireballBranch(abilityTree, (Fireball) weapon));
        branch.add(new FlameburstBranch(abilityTree, (FlameBurst) red));
        branch.add(new TimeWarpBranchPyromancer(abilityTree, (TimeWarpPyromancer) purple));
        branch.add(new ArcaneShieldBranch(abilityTree, (ArcaneShield) blue));
        branch.add(new InfernoBranch(abilityTree, (Inferno) orange));
    }
}
