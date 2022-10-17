package com.ebicep.warlords.pve.upgrades.warrior.revenant;

import com.ebicep.warlords.abilties.OrbsOfLife;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class OrbsOfLifeBranch extends AbstractUpgradeBranch<OrbsOfLife> {
    float cooldown = ability.getCooldown();
    float minHealing = ability.getMinDamageHeal();
    float maxHealing = ability.getMaxDamageHeal();

    public OrbsOfLifeBranch(AbilityTree abilityTree, OrbsOfLife ability) {
        super(abilityTree, ability);

        treeA.add(new Upgrade(
                "Alleviate - Tier I",
                "+7.5% Healing",
                5000,
                () -> {
                    ability.setMinDamageHeal(minHealing * 1.075f);
                    ability.setMaxDamageHeal(maxHealing * 1.075f);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier II",
                "+15% Healing",
                10000,
                () -> {
                    ability.setMinDamageHeal(minHealing * 1.15f);
                    ability.setMaxDamageHeal(maxHealing * 1.15f);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier III",
                "+22.5% Healing",
                15000,
                () -> {
                    ability.setMinDamageHeal(minHealing * 1.225f);
                    ability.setMaxDamageHeal(maxHealing * 1.225f);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier IV",
                "+30% Healing",
                20000,
                () -> {
                    ability.setMinDamageHeal(minHealing * 1.3f);
                    ability.setMaxDamageHeal(maxHealing * 1.3f);
                }
        ));

        treeB.add(new Upgrade(
                "Zeal - Tier I",
                "-5% Cooldown reduction",
                5000,
                () -> {
                    ability.setCooldown(cooldown * 0.95f);
                }
        ));
        treeB.add(new Upgrade(
                "Impair - Tier II",
                "-10% Cooldown reduction",
                10000,
                () -> {
                    ability.setCooldown(cooldown * 0.9f);
                }
        ));
        treeB.add(new Upgrade(
                "Impair - Tier III",
                "-15% Cooldown reduction",
                15000,
                () -> {
                    ability.setCooldown(cooldown * 0.85f);
                }
        ));
        treeB.add(new Upgrade(
                "Impair - Tier IV",
                "-20% Cooldown reduction",
                20000,
                () -> {
                    ability.setCooldown(cooldown * 0.8f);
                }
        ));

        masterUpgrade = new Upgrade(
                "Orbs of Relics",
                "Orbs of Life - Master Upgrade",
                "Spawn 1 additional orb on active, double\nhealing increase over time orbs last\ntwice as long.",
                50000,
                () -> {
                    ability.setPveUpgrade(true);
                    ability.setOrbTickMultiplier(2);
                }
        );
    }
}
