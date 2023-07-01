package com.ebicep.warlords.classes.shaman.specs;

import com.ebicep.warlords.abilities.*;
import com.ebicep.warlords.classes.shaman.AbstractShaman;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.shaman.earthwarden.*;

import java.util.List;

public class Earthwarden extends AbstractShaman {

    public Earthwarden() {
        super(
                "Earthwarden",
                5530,
                355,
                10,
                new EarthenSpike(),
                new Boulder(),
                new Earthliving(),
                new ChainHeal(),
                new HealingTotem()
        );
    }

    @Override
    public void setUpgradeBranches(WarlordsPlayer wp) {
        AbilityTree abilityTree = wp.getAbilityTree();
        List<AbstractUpgradeBranch<?>> branch = abilityTree.getUpgradeBranches();
        branch.add(new EarthenSpikeBranch(abilityTree, (EarthenSpike) weapon));
        branch.add(new BoulderBranch(abilityTree, (Boulder) red));
        branch.add(new EarthlivingWeaponBranch(abilityTree, (Earthliving) purple));
        branch.add(new ChainHealBranch(abilityTree, (ChainHeal) blue));
        branch.add(new HealingTotemBranch(abilityTree, (HealingTotem) orange));
    }
}