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
    int duration = ability.getDuration();

    public HealingTotemBranch(AbilityTree abilityTree, HealingTotem ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade(
                "Impair - Tier I",
                "+1 Block totem radius",
                5000,
                () -> {
                    ability.setRadius(radius + 1);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "+2 Blocks totem radius",
                10000,
                () -> {
                    ability.setRadius(radius + 2);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "+3 Blocks totem radius",
                15000,
                () -> {
                    ability.setRadius(radius + 3);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "+4 Blocks totem radius\n+20% Healing",
                20000,
                () -> {
                    ability.setRadius(radius + 4);
                    ability.setMinDamageHeal(minHealing * 1.2f);
                    ability.setMaxDamageHeal(maxHealing * 1.2f);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "+1s Duration",
                5000,
                () -> {
                    ability.setDuration(duration + 1);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "+2s Duration",
                10000,
                () -> {
                    ability.setDuration(duration + 2);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "+3s Duration",
                15000,
                () -> {
                    ability.setDuration(duration + 3);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "+4s Duration",
                20000,
                () -> {
                    ability.setDuration(duration + 4);
                }
        ));

        masterUpgrade = new Upgrade(
                "Healing Obelisk",
                "Healing Totem - Master Upgrade",
                "+2s Additional duration\n\nAll enemies within the radius of Healing Totem are\nperpetually crippled, reducing their damage dealt\nby 50%",
                50000,
                () -> {
                    ability.setDuration(ability.getDuration() + 2);
                    ability.setPveUpgrade(true);
                }
        );
    }
}
