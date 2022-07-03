package com.ebicep.warlords.classes.rogue.specs;

import com.ebicep.warlords.abilties.*;
import com.ebicep.warlords.classes.rogue.AbstractRogue;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.assassin.IncendiaryCureBranch;
import com.ebicep.warlords.pve.upgrades.assassin.JudgementStrikeBranch;
import com.ebicep.warlords.pve.upgrades.assassin.ShadowStepBranch;
import com.ebicep.warlords.pve.upgrades.assassin.SoulSwitchBranch;

import java.util.List;

public class Assassin extends AbstractRogue {

    public Assassin() {
        super(
                "Assassin",
                5200,
                305,
                0,
                new JudgementStrike(),
                new IncendiaryCurse(),
                new ShadowStep(),
                new SoulSwitch(),
                new OrderOfEviscerate()
        );
    }

    @Override
    public void setUpgradeBranches(WarlordsPlayer wp) {
        AbilityTree abilityTree = wp.getAbilityTree();
        List<AbstractUpgradeBranch<?>> branch = abilityTree.getUpgradeBranches();
        branch.add(new JudgementStrikeBranch(abilityTree, (JudgementStrike) weapon));
        branch.add(new IncendiaryCureBranch(abilityTree, (IncendiaryCurse) red));
        branch.add(new ShadowStepBranch(abilityTree, (ShadowStep) purple));
        branch.add(new SoulSwitchBranch(abilityTree, (SoulSwitch) blue));
    }
}
