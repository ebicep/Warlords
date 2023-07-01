package com.ebicep.warlords.classes.shaman.specs;

import com.ebicep.warlords.abilities.*;
import com.ebicep.warlords.classes.shaman.AbstractShaman;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.shaman.spiritguard.*;

import java.util.List;

public class Spiritguard extends AbstractShaman {

    public Spiritguard() {
        super(
                "Spiritguard",
                5530,
                305,
                10,
                new FallenSouls(),
                new SpiritLink(),
                new Soulbinding(),
                new Repentance(),
                new DeathsDebt()
        );
    }

    @Override
    public void setUpgradeBranches(WarlordsPlayer wp) {
        AbilityTree abilityTree = wp.getAbilityTree();
        List<AbstractUpgradeBranch<?>> branch = abilityTree.getUpgradeBranches();
        branch.add(new FallenSoulsBranch(abilityTree, (FallenSouls) weapon));
        branch.add(new SpiritLinkBranch(abilityTree, (SpiritLink) red));
        branch.add(new SoulbindingWeaponBranch(abilityTree, (Soulbinding) purple));
        branch.add(new RepentanceBranch(abilityTree, (Repentance) blue));
        branch.add(new DeathsDebtBranch(abilityTree, (DeathsDebt) orange));
    }
}
