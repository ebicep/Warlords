package com.ebicep.warlords.pve.upgrades.arcanist.conjurer;

import com.ebicep.warlords.abilties.AstralPlague;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class AstralPlagueBranch extends AbstractUpgradeBranch<AstralPlague> {

    float cooldown = ability.getCooldown();
    int tickDuration = ability.getTickDuration();

    public AstralPlagueBranch(AbilityTree abilityTree, AstralPlague ability) {
        super(abilityTree, ability);

        treeA.add(new Upgrade(
                "Zeal - Tier I",
                "5% Cooldown reduction",
                5000,
                () -> {
                    ability.setCooldown(cooldown * 0.95f);
                }
        ));
        treeA.add(new Upgrade(
                "Zeal - Tier II",
                "10% Cooldown reduction",
                10000,
                () -> {
                    ability.setCooldown(cooldown * 0.9f);
                }
        ));
        treeA.add(new Upgrade(
                "Zeal - Tier III",
                "15% Cooldown reduction",
                15000,
                () -> {
                    ability.setCooldown(cooldown * 0.85f);
                }
        ));
        treeA.add(new Upgrade(
                "Zeal - Tier IV",
                "20% Cooldown reduction",
                20000,
                () -> {
                    ability.setCooldown(cooldown * 0.8f);
                }
        ));

        treeB.add(new Upgrade(
                "Chronos - Tier I",
                "+1s Duration",
                5000,
                () -> {
                    ability.setTickDuration(tickDuration + 20);
                }
        ));
        treeB.add(new Upgrade(
                "Chronos - Tier II",
                "+2s Duration",
                10000,
                () -> {
                    ability.setTickDuration(tickDuration + 40);
                }
        ));
        treeB.add(new Upgrade(
                "Chronos - Tier III",
                "+3s Duration",
                15000,
                () -> {
                    ability.setTickDuration(tickDuration + 60);
                }
        ));
        treeB.add(new Upgrade(
                "Chronos - Tier IV",
                "+4s Duration",
                20000,
                () -> {
                    ability.setTickDuration(tickDuration + 80);
                }
        ));

        masterUpgrade = new Upgrade(
                "Electrifying Storm",
                "Healing Rain - Master Upgrade",
                """
                        """,
                50000,
                () -> {

                }
        );
    }

}
