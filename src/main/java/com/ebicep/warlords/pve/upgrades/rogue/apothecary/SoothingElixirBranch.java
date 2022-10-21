package com.ebicep.warlords.pve.upgrades.rogue.apothecary;

import com.ebicep.warlords.abilties.SoothingElixir;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class SoothingElixirBranch extends AbstractUpgradeBranch<SoothingElixir> {

    float minHealing = ability.getMinDamageHeal();
    float maxHealing = ability.getMaxDamageHeal();
    float puddleRadius = ability.getPuddleRadius();
    float cooldown = ability.getCooldown();

    public SoothingElixirBranch(AbilityTree abilityTree, SoothingElixir ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade(
                "Impair - Tier I",
                "+7.5% Healing",
                5000,
                () -> {
                    ability.setMinDamageHeal(minHealing * 1.075f);
                    ability.setMaxDamageHeal(maxHealing * 1.075f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "+15% Healing",
                10000,
                () -> {
                    ability.setMinDamageHeal(minHealing * 1.15f);
                    ability.setMaxDamageHeal(maxHealing * 1.15f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "+22.5% Healing",
                15000,
                () -> {
                    ability.setMinDamageHeal(minHealing * 1.225f);
                    ability.setMaxDamageHeal(maxHealing * 1.225f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "+30% Healing",
                20000,
                () -> {
                    ability.setMinDamageHeal(minHealing * 1.3f);
                    ability.setMaxDamageHeal(maxHealing * 1.3f);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "+1.5 Blocks puddle radius",
                5000,
                () -> {
                    ability.setPuddleRadius(puddleRadius + 1.5f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "+3 Blocks puddle radius",
                10000,
                () -> {
                    ability.setPuddleRadius(puddleRadius + 3);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "+4.5 Blocks puddle radius",
                15000,
                () -> {
                    ability.setPuddleRadius(puddleRadius + 4.5f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "+6 Blocks puddle radius\n+2s Duration",
                20000,
                () -> {
                    ability.setPuddleRadius(puddleRadius + 6);
                    ability.setPuddleDuration(ability.getPuddleDuration() + 2);
                }
        ));

        masterUpgrade = new Upgrade(
                "Alleviating Elixir",
                "Soothing Elixir - Master Upgrade",
                "Soothing Puddle now heals every 0.5s instead\nof 1s within the puddle radius and inflicts\nLEECH on all enemies hit by the elixir impact.",
                50000,
                () -> {
                    ability.setPveUpgrade(true);
                }
        );
    }
}
