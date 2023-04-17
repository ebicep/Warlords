package com.ebicep.warlords.classes.warrior.specs;

import com.ebicep.warlords.abilties.*;
import com.ebicep.warlords.classes.warrior.AbstractWarrior;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.warrior.GroundSlamBranch;
import com.ebicep.warlords.pve.upgrades.warrior.SeismicWaveBranch;
import com.ebicep.warlords.pve.upgrades.warrior.berserker.BerserkBranch;
import com.ebicep.warlords.pve.upgrades.warrior.berserker.BloodlustBranch;
import com.ebicep.warlords.pve.upgrades.warrior.berserker.WoundingStrikeBranchBers;

import java.util.List;

public class Berserker extends AbstractWarrior {

    public Berserker() {
        super(
                "Berserker",
                6300,
                305,
                0,
                new WoundingStrikeBerserker(),
                new SeismicWave("Seismic Wave", 557, 753, 11.74f, 60, 25, 200),
                new GroundSlam(448.8f, 606.1f, 9.32f, 60, 15, 200),
                new BloodLust(),
                new Berserk()
        );
    }

    @Override
    public void setUpgradeBranches(WarlordsPlayer wp) {
        AbilityTree abilityTree = wp.getAbilityTree();
        List<AbstractUpgradeBranch<?>> branch = abilityTree.getUpgradeBranches();
        branch.add(new WoundingStrikeBranchBers(abilityTree, (WoundingStrikeBerserker) weapon));
        branch.add(new SeismicWaveBranch(abilityTree, (SeismicWave) red));
        branch.add(new GroundSlamBranch(abilityTree, (GroundSlam) purple));
        branch.add(new BloodlustBranch(abilityTree, (BloodLust) blue));
        branch.add(new BerserkBranch(abilityTree, (Berserk) orange));
    }
}
