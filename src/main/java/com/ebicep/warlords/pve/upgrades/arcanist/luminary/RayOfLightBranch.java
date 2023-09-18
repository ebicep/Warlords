package com.ebicep.warlords.pve.upgrades.arcanist.luminary;

import com.ebicep.warlords.abilities.RayOfLight;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class RayOfLightBranch extends AbstractUpgradeBranch<RayOfLight> {

    float cooldown = ability.getCooldown();
    float minDamage;
    float maxDamage;

    public RayOfLightBranch(AbilityTree abilityTree, RayOfLight ability) {
        super(abilityTree, ability);
        if (abilityTree.getWarlordsPlayer().isInPve()) {
            ability.multiplyMinMax(1.3f);
        }
        minDamage = ability.getMinDamageHeal();
        maxDamage = ability.getMaxDamageHeal();

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
                "Alleviate - Tier I",
                "+10% Healing",
                5000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.1f);
                    ability.setMaxDamageHeal(maxDamage * 1.1f);
                }
        ));
        treeB.add(new Upgrade(
                "Alleviate - Tier II",
                "+20% Healing",
                10000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.2f);
                    ability.setMaxDamageHeal(maxDamage * 1.2f);
                }
        ));
        treeB.add(new Upgrade(
                "Alleviate - Tier III",
                "+30% Healing",
                15000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.3f);
                    ability.setMaxDamageHeal(maxDamage * 1.3f);
                }
        ));
        treeB.add(new Upgrade(
                "Alleviate - Tier IV",
                "+40% Healing",
                20000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.4f);
                    ability.setMaxDamageHeal(maxDamage * 1.4f);
                }
        ));

        masterUpgrade = new Upgrade(
                "Divine Light",
                "Ray of Light - Master Upgrade",
                """
                        Ray of Light will grant allies with max stacks of Merciful Hex a 10% damage bonus for 5s. Additionally, increase bonus healing for max stack allies by 25%.
                        """,
                50000,
                () -> {
                    ability.setHealingIncrease(ability.getHealingIncrease() + 25);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Volatile Rays",
                "Ray of Light - Master Upgrade",
                """
                        Ray of Light fires two additional beams.
                        """,
                50000,
                () -> {
                    ability.setShotsFiredAtATime(3);
                }
        );
    }

}
