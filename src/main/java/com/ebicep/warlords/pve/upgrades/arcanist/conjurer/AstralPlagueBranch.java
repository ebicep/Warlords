package com.ebicep.warlords.pve.upgrades.arcanist.conjurer;

import com.ebicep.warlords.abilities.AstralPlague;
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
                "-5% Cooldown reduction",
                5000,
                () -> {
                    ability.setCooldown(cooldown * 0.95f);
                }
        ));
        treeA.add(new Upgrade(
                "Zeal - Tier II",
                "-10% Cooldown reduction",
                10000,
                () -> {
                    ability.setCooldown(cooldown * 0.9f);
                }
        ));
        treeA.add(new Upgrade(
                "Zeal - Tier III",
                "-15% Cooldown reduction",
                15000,
                () -> {
                    ability.setCooldown(cooldown * 0.85f);
                }
        ));
        treeA.add(new Upgrade(
                "Zeal - Tier IV",
                "-20% Cooldown reduction",
                20000,
                () -> {
                    ability.setCooldown(cooldown * 0.8f);
                }
        ));

        treeB.add(new Upgrade(
                "Chronos - Tier I",
                "+1.5s Duration",
                5000,
                () -> {
                    ability.setTickDuration(tickDuration + 30);
                }
        ));
        treeB.add(new Upgrade(
                "Chronos - Tier II",
                "+3s Duration",
                10000,
                () -> {
                    ability.setTickDuration(tickDuration + 60);
                }
        ));
        treeB.add(new Upgrade(
                "Chronos - Tier III",
                "+4.5s Duration",
                15000,
                () -> {
                    ability.setTickDuration(tickDuration + 90);
                }
        ));
        treeB.add(new Upgrade(
                "Chronos - Tier IV",
                "+6s Duration",
                20000,
                () -> {
                    ability.setTickDuration(tickDuration + 120);
                }
        ));

        masterUpgrade = new Upgrade(
                "Virulent State",
                "Astral Plague - Master Upgrade",
                """
                        For the duration of Astral Plague, increase Crit Multiplier by 40%, and Soulfire Beam is guaranteed to crit on enemies with max Hex stacks.
                        """,
                50000,
                () -> {

                }
        );
    }

}
