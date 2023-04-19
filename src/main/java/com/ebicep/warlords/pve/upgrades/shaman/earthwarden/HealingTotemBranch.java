package com.ebicep.warlords.pve.upgrades.shaman.earthwarden;

import com.ebicep.warlords.abilties.HealingTotem;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class HealingTotemBranch extends AbstractUpgradeBranch<HealingTotem> {

    float minHealing = ability.getMinDamageHeal();
    float maxHealing = ability.getMaxDamageHeal();
    float cooldown = ability.getCooldown();
    int radius = ability.getRadius();

    public HealingTotemBranch(AbilityTree abilityTree, HealingTotem ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade(
                "Scope - Tier I",
                "+2 Block totem radius",
                5000,
                () -> {
                    ability.setRadius(radius + 2);
                }
        ));
        treeA.add(new Upgrade(
                "Scope - Tier II",
                "+4 Blocks totem radius",
                10000,
                () -> {
                    ability.setRadius(radius + 4);
                }
        ));
        treeA.add(new Upgrade(
                "Scope - Tier III",
                "+6 Blocks totem radius",
                15000,
                () -> {
                    ability.setRadius(radius + 6);
                }
        ));
        treeA.add(new Upgrade(
                "Scope - Tier IV",
                "+8 Blocks totem radius\n+20% Healing",
                20000,
                () -> {
                    ability.setRadius(radius + 8);
                    ability.setMinDamageHeal(minHealing * 1.2f);
                    ability.setMaxDamageHeal(maxHealing * 1.2f);
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
                "Healing Obelisk",
                "Healing Totem - Master Upgrade",
                """
                        Double the duration of Healing Totem but reduce the incremental healing by 15%.

                        All enemies within the radius of Healing Totem are perpetually slowed and crippled, reducing their movement speed and damage dealt by 50%. Additionally, reduce their passive damage resistance by 5% for each second they are in range of your Healing Totem.""",
                50000,
                () -> {
                    ability.setHealingIncrement(20);
                    ability.setTickDuration(ability.getTickDuration() * 2);
                    ability.setPveUpgrade(true);
                }
        );
    }
}
