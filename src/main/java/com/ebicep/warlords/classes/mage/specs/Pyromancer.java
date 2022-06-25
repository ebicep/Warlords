package com.ebicep.warlords.classes.mage.specs;

import com.ebicep.warlords.abilties.*;
import com.ebicep.warlords.classes.mage.AbstractMage;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.FireballUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.UpgradeBranch;

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
    public void setUpgradeBranches(WarlordsPlayer warlordsPlayer) {
        AbilityTree abilityTree = warlordsPlayer.getAbilityTree();
        List<UpgradeBranch<?>> upgradeBranches = abilityTree.getUpgradeBranches();
        upgradeBranches.add(new FireballUpgradeBranch(abilityTree, (Fireball) weapon, warlordsPlayer.getItemStackForAbility(weapon)));
    }
}
