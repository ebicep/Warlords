package com.ebicep.warlords.classes.paladin.specs;

import com.ebicep.warlords.abilties.*;
import com.ebicep.warlords.classes.paladin.AbstractPaladin;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.avenger.AvengerStrikeBranch;
import com.ebicep.warlords.pve.upgrades.avenger.ConsecrateBranch;
import com.ebicep.warlords.pve.upgrades.avenger.LightInfusionBranch;

import java.util.List;

public class Avenger extends AbstractPaladin {

    public Avenger() {
        super(
                "Avenger",
                6300,
                305,
                0,
                new AvengersStrike(),
                new Consecrate(158.4f, 213.6f, 50, 20, 175, 20, 5),
                new LightInfusion(15.66f),
                new HolyRadianceAvenger(582, 760, 19.57f, 20, 15, 175),
                new AvengersWrath()
        );
    }

    @Override
    public void setUpgradeBranches(WarlordsPlayer wp) {
        AbilityTree abilityTree = wp.getAbilityTree();
        List<AbstractUpgradeBranch<?>> branch = abilityTree.getUpgradeBranches();
        branch.add(new AvengerStrikeBranch(abilityTree, (AvengersStrike) weapon));
        branch.add(new ConsecrateBranch(abilityTree, (Consecrate) red));
        branch.add(new LightInfusionBranch(abilityTree, (LightInfusion) purple));
    }
}
