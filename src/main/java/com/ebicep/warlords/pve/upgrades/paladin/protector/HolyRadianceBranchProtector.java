package com.ebicep.warlords.pve.upgrades.paladin.protector;

import com.ebicep.warlords.abilities.HolyRadianceProtector;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class HolyRadianceBranchProtector extends AbstractUpgradeBranch<HolyRadianceProtector> {

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();
    float cooldown = ability.getCooldown();
    float markHealing = ability.getMarkHealing();

    public HolyRadianceBranchProtector(AbilityTree abilityTree, HolyRadianceProtector ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade(
                "Alleviate - Tier I",
                "+10% Healing",
                5000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.1f);
                    ability.setMaxDamageHeal(maxDamage * 1.1f);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier II",
                "+20% Healing",
                10000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.2f);
                    ability.setMaxDamageHeal(maxDamage * 1.2f);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier III",
                "+30% Healing",
                15000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.3f);
                    ability.setMaxDamageHeal(maxDamage * 1.3f);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier IV",
                "+40% Healing\n-30 Energy cost",
                20000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.4f);
                    ability.setMaxDamageHeal(maxDamage * 1.4f);
                    ability.setEnergyCost(ability.getEnergyCost() - 30);
                }
        ));

        treeB.add(new Upgrade(
                "Zeal - Tier I",
                "+10% Additional mark healing",
                5000,
                () -> {
                    ability.setMarkHealing(markHealing + 10);
                }
        ));
        treeB.add(new Upgrade(
                "Zeal - Tier II",
                "+20% Additional mark healing",
                10000,
                () -> {
                    ability.setMarkHealing(markHealing + 20);
                }
        ));
        treeB.add(new Upgrade(
                "Zeal - Tier III",
                "+30% Additional mark healing",
                15000,
                () -> {
                    ability.setMarkHealing(markHealing + 30);
                }
        ));
        treeB.add(new Upgrade(
                "Zeal - Tier IV",
                "+40% Additional mark healing\n-20% Cooldown reduction",
                20000,
                () -> {
                    ability.setMarkHealing(markHealing + 40);
                    ability.setCooldown(cooldown * 0.8f);
                }
        ));

        masterUpgrade = new Upgrade(
                "Edifying Radiance",
                "Holy Radiance - Master Upgrade",
                "Protector's Mark is now AoE and has no target limit.",
                50000,
                () -> {

                }
        );
    }
}
