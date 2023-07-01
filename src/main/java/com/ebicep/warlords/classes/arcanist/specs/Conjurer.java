package com.ebicep.warlords.classes.arcanist.specs;

import com.ebicep.warlords.abilities.*;
import com.ebicep.warlords.classes.arcanist.AbstractArcanist;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;

import java.util.List;

public class Conjurer extends AbstractArcanist {

    public Conjurer() {
        super(
                "Conjurer",
                5200,
                305,
                20,
                14,
                0,
                new PoisonousHex(),
                new SoulfireBeam(),
                new EnergySeerConjurer(),
                new ContagiousFacade(),
                new AstralPlague()
        );
    }

    @Override
    public void setUpgradeBranches(WarlordsPlayer wp) {
        AbilityTree abilityTree = wp.getAbilityTree();
        List<AbstractUpgradeBranch<?>> branch = abilityTree.getUpgradeBranches();

    }
}
