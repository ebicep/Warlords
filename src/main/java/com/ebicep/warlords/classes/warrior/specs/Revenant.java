package com.ebicep.warlords.classes.warrior.specs;

import com.ebicep.warlords.abilties.*;
import com.ebicep.warlords.classes.warrior.AbstractWarrior;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.warrior.GroundSlamBranch;
import com.ebicep.warlords.pve.upgrades.warrior.revenant.CripplingStrikeBranch;
import com.ebicep.warlords.pve.upgrades.warrior.revenant.OrbsOfLifeBranch;
import com.ebicep.warlords.pve.upgrades.warrior.revenant.RecklessChargeBranch;
import com.ebicep.warlords.pve.upgrades.warrior.revenant.UndyingArmyBranch;

import java.util.List;

public class Revenant extends AbstractWarrior {

    public Revenant() {
        super(
                "Revenant",
                6300,
                305,
                0,
                new CripplingStrike(),
                new RecklessCharge(),
                new GroundSlam(326, 441, 9.32f, 30, 35, 200),
                new OrbsOfLife(),
                new UndyingArmy()
        );
    }

    @Override
    public void setUpgradeBranches(WarlordsPlayer wp) {
        AbilityTree abilityTree = wp.getAbilityTree();
        List<AbstractUpgradeBranch<?>> branch = abilityTree.getUpgradeBranches();
        branch.add(new CripplingStrikeBranch(abilityTree, (CripplingStrike) weapon));
        branch.add(new RecklessChargeBranch(abilityTree, (RecklessCharge) red));
        branch.add(new GroundSlamBranch(abilityTree, (GroundSlam) purple));
        branch.add(new OrbsOfLifeBranch(abilityTree, (OrbsOfLife) blue));
        branch.add(new UndyingArmyBranch(abilityTree, (UndyingArmy) orange));
    }
}
