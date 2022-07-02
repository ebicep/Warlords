package com.ebicep.warlords.classes.paladin.specs;

import com.ebicep.warlords.abilties.*;
import com.ebicep.warlords.classes.paladin.AbstractPaladin;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.avenger.ConsecrateBranch;
import com.ebicep.warlords.pve.upgrades.avenger.LightInfusionBranch;

import java.util.List;

public class Protector extends AbstractPaladin {

    public Protector() {
        super(
                "Protector",
                5750,
                385,
                0,
                new ProtectorsStrike(),
                new Consecrate(96, 130, 10, 15, 200, 15, 4),
                new LightInfusion(15.66f),
                new HolyRadianceProtector(582, 760, 9.87f, 60, 15, 175),
                new HammerOfLight()
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
