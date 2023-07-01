package com.ebicep.warlords.classes.warrior.specs;

import com.ebicep.warlords.abilities.*;
import com.ebicep.warlords.classes.warrior.AbstractWarrior;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.warrior.GroundSlamBranch;
import com.ebicep.warlords.pve.upgrades.warrior.SeismicWaveBranch;
import com.ebicep.warlords.pve.upgrades.warrior.defender.InterveneBranch;
import com.ebicep.warlords.pve.upgrades.warrior.defender.LastStandBranch;
import com.ebicep.warlords.pve.upgrades.warrior.defender.WoundingStrikeBranchDef;

import java.util.List;

public class Defender extends AbstractWarrior {

    public Defender() {
        super(
                "Defender",
                7400,
                305,
                10,
                new WoundingStrikeDefender(),
                new SeismicWave("Seismic Wave", 506, 685, 11.74f, 60, 25, 200),
                new GroundSlam(326, 441, 7.34f, 0, 15, 200),
                new Intervene(),
                new LastStand()
        );
    }

    @Override
    public void setUpgradeBranches(WarlordsPlayer wp) {
        AbilityTree abilityTree = wp.getAbilityTree();
        List<AbstractUpgradeBranch<?>> branch = abilityTree.getUpgradeBranches();
        branch.add(new WoundingStrikeBranchDef(abilityTree, (WoundingStrikeDefender) weapon));
        branch.add(new SeismicWaveBranch(abilityTree, (SeismicWave) red));
        branch.add(new GroundSlamBranch(abilityTree, (GroundSlam) purple));
        branch.add(new InterveneBranch(abilityTree, (Intervene) blue));
        branch.add(new LastStandBranch(abilityTree, (LastStand) orange));
    }
}
