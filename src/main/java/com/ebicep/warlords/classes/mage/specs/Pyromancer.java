package com.ebicep.warlords.classes.mage.specs;

import com.ebicep.warlords.abilties.*;
import com.ebicep.warlords.classes.mage.AbstractMage;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.pyromancer.*;

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
                new TimeWarp(),
                new ArcaneShield(),
                new Inferno()
        );
    }

    @Override
    public void setUpgradeBranches(WarlordsPlayer wp) {
        AbilityTree abilityTree = wp.getAbilityTree();
        List<AbstractUpgradeBranch<?>> branch = abilityTree.getUpgradeBranches();
        branch.add(new FireballBranch(abilityTree, (Fireball) weapon, wp.getItemStackForAbility(weapon), weapon.getName()));
        branch.add(new FlameburstBranch(abilityTree, (FlameBurst) red, wp.getItemStackForAbility(red), red.getName()));
        branch.add(new TimeWarpBranch(abilityTree, (TimeWarp) purple, wp.getItemStackForAbility(purple), purple.getName()));
        branch.add(new ArcaneShieldBranch(abilityTree, (ArcaneShield) blue, wp.getItemStackForAbility(blue), blue.getName()));
        branch.add(new InfernoBranch(abilityTree, (Inferno) orange, wp.getItemStackForAbility(orange), orange.getName()));
    }
}
