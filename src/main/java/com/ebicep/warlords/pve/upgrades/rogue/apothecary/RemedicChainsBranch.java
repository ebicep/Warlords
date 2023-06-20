package com.ebicep.warlords.pve.upgrades.rogue.apothecary;

import com.ebicep.warlords.abilties.RemedicChains;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class RemedicChainsBranch extends AbstractUpgradeBranch<RemedicChains> {

    float minHealing = ability.getMinDamageHeal();
    float maxHealing = ability.getMaxDamageHeal();
    float cooldown = ability.getCooldown();

    public RemedicChainsBranch(AbilityTree abilityTree, RemedicChains ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade(
                "Alleviating - Tier I",
                "+7.5% Healing",
                5000,
                () -> {
                    ability.setMinDamageHeal(minHealing * 1.075f);
                    ability.setMaxDamageHeal(maxHealing * 1.075f);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviating - Tier II",
                "+15% Healing",
                10000,
                () -> {
                    ability.setMinDamageHeal(minHealing * 1.15f);
                    ability.setMaxDamageHeal(maxHealing * 1.15f);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviating - Tier III",
                "+22.5% Healing",
                15000,
                () -> {
                    ability.setMinDamageHeal(minHealing * 1.225f);
                    ability.setMaxDamageHeal(maxHealing * 1.225f);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviating - Tier IV",
                "+30% Healing",
                20000,
                () -> {
                    ability.setMinDamageHeal(minHealing * 1.3f);
                    ability.setMaxDamageHeal(maxHealing * 1.3f);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "-5% Cooldown reduction",
                5000,
                () -> {
                    ability.setCooldown(cooldown * 0.95f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "-10% Cooldown reduction",
                10000,
                () -> {
                    ability.setCooldown(cooldown * 0.9f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "-15% Cooldown reduction",
                15000,
                () -> {
                    ability.setCooldown(cooldown * 0.85f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "-20% Cooldown reduction",
                20000,
                () -> {
                    ability.setCooldown(cooldown * 0.8f);
                }
        ));

        masterUpgrade = new Upgrade(
                "Crystallizing Chains",
                "Remedic Chains - Master Upgrade",
                "Increase bonus damage dealt by an additional 8% and temporarily increase all linked allies' max health by 25%.",
                50000,
                () -> {
                    ability.setPveMasterUpgrade(true);
                    ability.setAllyDamageIncrease(20);
                }
        );
    }
}
