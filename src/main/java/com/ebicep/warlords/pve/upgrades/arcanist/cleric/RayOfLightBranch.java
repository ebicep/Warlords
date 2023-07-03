package com.ebicep.warlords.pve.upgrades.arcanist.cleric;

import com.ebicep.warlords.abilities.RayOfLight;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class RayOfLightBranch extends AbstractUpgradeBranch<RayOfLight> {

    float cooldown = ability.getCooldown();
    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();

    public RayOfLightBranch(AbilityTree abilityTree, RayOfLight ability) {
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
                "+40% Healing\n-30 Energy cost",
                20000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.4f);
                    ability.setMaxDamageHeal(maxDamage * 1.4f);
                    ability.setEnergyCost(ability.getEnergyCost() - 30);
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
