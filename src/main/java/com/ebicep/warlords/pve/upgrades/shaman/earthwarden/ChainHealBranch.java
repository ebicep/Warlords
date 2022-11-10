package com.ebicep.warlords.pve.upgrades.shaman.earthwarden;

import com.ebicep.warlords.abilties.ChainHeal;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class ChainHealBranch extends AbstractUpgradeBranch<ChainHeal> {

    float minHealing = ability.getMinDamageHeal();
    float maxHealing = ability.getMaxDamageHeal();
    float energyCost = ability.getEnergyCost();
    float cooldown = ability.getCooldown();

    public ChainHealBranch(AbilityTree abilityTree, ChainHeal ability) {
        super(abilityTree, ability);

        treeA.add(new Upgrade(
                "Alleviate - Tier I",
                "+10% Healing",
                5000,
                () -> {
                    ability.setMinDamageHeal(minHealing * 1.1f);
                    ability.setMaxDamageHeal(maxHealing * 1.1f);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier II",
                "+20% Healing",
                10000,
                () -> {
                    ability.setMinDamageHeal(minHealing * 1.2f);
                    ability.setMaxDamageHeal(maxHealing * 1.2f);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier III",
                "+30% Healing",
                15000,
                () -> {
                    ability.setMinDamageHeal(minHealing * 1.3f);
                    ability.setMaxDamageHeal(maxHealing * 1.3f);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier IV",
                "+40% Healing",
                20000,
                () -> {
                    ability.setMinDamageHeal(minHealing * 1.4f);
                    ability.setMaxDamageHeal(maxHealing * 1.4f);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "-5 Energy cost",
                5000,
                () -> {
                    ability.setEnergyCost(energyCost - 5);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "-10 Energy cost",
                10000,
                () -> {
                    ability.setEnergyCost(energyCost - 10);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "-15 Energy cost\n-10% Cooldown reduction",
                15000,
                () -> {
                    ability.setEnergyCost(energyCost - 15);
                    ability.setCooldown(cooldown * 0.9f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "-20 Energy cost\n-20% Cooldown reduction",
                20000,
                () -> {
                    ability.setEnergyCost(energyCost - 20);
                    ability.setCooldown(cooldown * 0.8f);
                }
        ));

        masterUpgrade = new Upgrade(
                "Healing Chains",
                "Chain Heal - Master Upgrade",
                "+1 Chain bounce\n+25% Additional healing\n+5 Blocks cast and bounce range.",
                50000,
                () -> {
                    ability.setPveUpgrade(true);
                    ability.setBounceRange(ability.getBounceRange() + 5);
                    ability.setRadius(ability.getRadius() + 5);
                    ability.setMinDamageHeal(ability.getMinDamageHeal() * 1.25f);
                    ability.setMaxDamageHeal(ability.getMaxDamageHeal() * 1.25f);
                }
        );
    }
}
