package com.ebicep.warlords.classes.rogue.specs;

import com.ebicep.warlords.abilties.*;
import com.ebicep.warlords.classes.rogue.AbstractRogue;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.rogue.vindicator.RighteousStrikeBranch;
import com.ebicep.warlords.pve.upgrades.rogue.vindicator.SoulShackleBranch;

import java.util.List;

public class Vindicator extends AbstractRogue {

    public Vindicator() {
        super(
                "Vindicator",
                6000,
                305,
                15,
                new RighteousStrike(),
                new SoulShackle(),
                new HeartToHeart(),
                new PrismGuard(),
                new Vindicate()
        );
    }

    @Override
    public void setUpgradeBranches(WarlordsPlayer wp) {
        AbilityTree abilityTree = wp.getAbilityTree();
        List<AbstractUpgradeBranch<?>> branch = abilityTree.getUpgradeBranches();
        branch.add(new RighteousStrikeBranch(abilityTree, (RighteousStrike) weapon));
        branch.add(new SoulShackleBranch(abilityTree, (SoulShackle) red));
    }
}
