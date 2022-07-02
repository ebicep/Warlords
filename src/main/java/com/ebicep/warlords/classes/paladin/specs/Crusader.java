package com.ebicep.warlords.classes.paladin.specs;

import com.ebicep.warlords.abilties.*;
import com.ebicep.warlords.classes.paladin.AbstractPaladin;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.avenger.ConsecrateBranch;
import com.ebicep.warlords.pve.upgrades.avenger.LightInfusionBranch;

import java.util.List;

public class Crusader extends AbstractPaladin {

    public Crusader() {
        super(
                "Crusader",
                6850,
                305,
                20,
                new CrusadersStrike(),
                new Consecrate(144, 194.4f, 50, 15, 200, 15, 4),
                new LightInfusion(15.66f),
                new HolyRadianceCrusader(582, 760, 19.57f, 20, 15, 175),
                new InspiringPresence()
        );
    }

    @Override
    public void setUpgradeBranches(WarlordsPlayer wp) {
        AbilityTree abilityTree = wp.getAbilityTree();
        List<AbstractUpgradeBranch<?>> branch = abilityTree.getUpgradeBranches();
        branch.add(new ConsecrateBranch(abilityTree, (Consecrate) red));
        branch.add(new LightInfusionBranch(abilityTree, (LightInfusion) purple));
    }
}
