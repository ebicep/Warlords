package com.ebicep.warlords.classes.rogue.specs;

import com.ebicep.warlords.abilties.*;
import com.ebicep.warlords.classes.rogue.AbstractRogue;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.rogue.apothecary.*;

import java.util.List;

public class Apothecary extends AbstractRogue {

    public Apothecary() {
        super(
                "Apothecary",
                5750,
                375,
                0,
                new ImpalingStrike(),
                new SoothingElixir(),
                new VitalityLiquor(),
                new RemedicChains(),
                new DrainingMiasma()
        );
    }

    @Override
    public void setUpgradeBranches(WarlordsPlayer wp) {
        AbilityTree abilityTree = wp.getAbilityTree();
        List<AbstractUpgradeBranch<?>> branch = abilityTree.getUpgradeBranches();
        branch.add(new ImpalingStrikeBranch(abilityTree, (ImpalingStrike) weapon));
        branch.add(new SoothingElixirBranch(abilityTree, (SoothingElixir) red));
        branch.add(new VitalityLiquorBranch(abilityTree, (VitalityLiquor) purple));
        branch.add(new RemedicChainsBranch(abilityTree, (RemedicChains) blue));
        branch.add(new DrainingMiasmaBranch(abilityTree, (DrainingMiasma) orange));
    }
}
