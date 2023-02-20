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
                "+12.5% Healing",
                5000,
                () -> {
                    ability.setMinDamageHeal(minHealing * 1.125f);
                    ability.setMaxDamageHeal(maxHealing * 1.125f);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier II",
                "+25% Healing",
                10000,
                () -> {
                    ability.setMinDamageHeal(minHealing * 1.25f);
                    ability.setMaxDamageHeal(maxHealing * 1.25f);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier III",
                "+37.5% Healing",
                15000,
                () -> {
                    ability.setMinDamageHeal(minHealing * 1.375f);
                    ability.setMaxDamageHeal(maxHealing * 1.375f);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier IV",
                "+50% Healing",
                20000,
                () -> {
                    ability.setMinDamageHeal(minHealing * 1.5f);
                    ability.setMaxDamageHeal(maxHealing * 1.5f);
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
                "Zeal - Tier II",
                "-10% Cooldown reduction",
                10000,
                () -> {
                    ability.setCooldown(cooldown * 0.9f);
                }
        ));
        treeB.add(new Upgrade(
                "Zeal - Tier III",
                "-15% Cooldown reduction",
                15000,
                () -> {
                    ability.setCooldown(cooldown * 0.85f);
                }
        ));
        treeB.add(new Upgrade(
                "Zeal - Tier IV",
                "-20% Cooldown reduction",
                20000,
                () -> {
                    ability.setCooldown(cooldown * 0.8f);
                }
        ));

        masterUpgrade = new Upgrade(
                "Orbs of Relics",
                "Orbs of Life - Master Upgrade",
                "Spawn 1 additional orb on active, double healing increase over time orbs last twice as long.",
                50000,
                () -> {
                    ability.setPveUpgrade(true);
                    ability.setOrbTickMultiplier(2);
                }
        );
    }
}
