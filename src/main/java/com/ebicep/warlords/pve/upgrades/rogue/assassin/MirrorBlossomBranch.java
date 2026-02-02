package com.ebicep.warlords.pve.upgrades.rogue.assassin;

import com.ebicep.warlords.abilities.MirrorBlossom;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeTreeBuilder;

public class MirrorBlossomBranch extends AbstractUpgradeBranch<MirrorBlossom> {

    public MirrorBlossomBranch(AbilityTree abilityTree, MirrorBlossom ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeHitBox(ability, 0.5f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeCooldown(ability)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Marked for Blossom",
                "Mirror Blossom - Master Upgrade",
                "Duration is doubled and the damage ticks twice as fast. Additionally, each subsequent hit causes your Judgement Strike to deal 5% (max 100%) more damage per hit they took from Mirror Blossom.",
                50000,
                () -> {
                    ability.setTickDuration(ability.getTickDuration() * 2);
                }
        );

        masterUpgrade2 = new Upgrade(
                "Mirror of Judgement",
                "Mirror Blossom - Master Upgrade",
                """
                        +20% Speed
                        
                        Enemies standing in your Mirror Blossom will be marked. Causing your Judgement Strike to chain to 2 additional targets within 6 blocks. When Mirror Blossom ends it will explode. Dealing 2205 - 2989 true damage to all enemies within range.
                        """,
                50000,
                () -> {
                    abilityTree.getWarlordsPlayer().getSpeed().addBaseModifier(20);
                }
        );
    }
}
