package com.ebicep.warlords.pve.upgrades.arcanist.sentinel;

import com.ebicep.warlords.abilities.Sanctuary;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class SanctuaryBranch extends AbstractUpgradeBranch<Sanctuary> {

    float cooldown = ability.getCooldown();
    int hexTickDurationIncrease = ability.getHexTickDurationIncrease();

    public SanctuaryBranch(AbilityTree abilityTree, Sanctuary ability) {
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
                "+0.5s Hex duration",
                5000,
                () -> {
                    ability.setHexTickDurationIncrease(hexTickDurationIncrease + 10);
                }
        ));
        treeB.add(new Upgrade(
                "Chronos - Tier II",
                "+1s Hex duration",
                10000,
                () -> {
                    ability.setHexTickDurationIncrease(hexTickDurationIncrease + 20);
                }
        ));
        treeB.add(new Upgrade(
                "Chronos - Tier III",
                "+1.5s Hex duration",
                15000,
                () -> {
                    ability.setHexTickDurationIncrease(hexTickDurationIncrease + 30);
                }
        ));
        treeB.add(new Upgrade(
                "Chronos - Tier IV",
                "+2s Hex duration",
                20000,
                () -> {
                    ability.setHexTickDurationIncrease(hexTickDurationIncrease + 40);
                }
        ));

        masterUpgrade = new Upgrade(
                "Oasis",
                "Sanctuary - Master Upgrade",
                """
                        All allies with max stacks of Fortifying Hex gain an additional 15% damage reduction. Reflection damage ignores enemy defenses and resistances.
                        """,
                50000,
                () -> {
                    ability.setAdditionalDamageReduction(ability.getAdditionalDamageReduction() + 15);
                }
        );
    }

}
