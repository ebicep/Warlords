package com.ebicep.warlords.pve.upgrades.rogue.apothecary;

import com.ebicep.warlords.abilities.VitalityLiquor;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class VitalityLiquorBranch extends AbstractUpgradeBranch<VitalityLiquor> {

    int energyPerSecond = ability.getEnergyPerSecond();
    float cooldown = ability.getCooldown();
    float minHealing = ability.getMinDamageHeal();
    float maxHealing = ability.getMaxDamageHeal();
    float minWaveHealing = ability.getMinWaveHealing();
    float maxWaveHealing = ability.getMaxWaveHealing();
    int vitalityRange = ability.getVitalityRange();

    public VitalityLiquorBranch(AbilityTree abilityTree, VitalityLiquor ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade(
                "Impair - Tier I",
                "+10% Healing",
                5000,
                () -> {
                    ability.setMinWaveHealing(minWaveHealing * 1.1f);
                    ability.setMaxWaveHealing(maxWaveHealing * 1.1f);
                    ability.setMinDamageHeal(minHealing * 1.1f);
                    ability.setMaxDamageHeal(maxHealing * 1.1f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "+20% Healing",
                10000,
                () -> {
                    ability.setMinWaveHealing(minWaveHealing * 1.2f);
                    ability.setMaxWaveHealing(maxWaveHealing * 1.2f);
                    ability.setMinDamageHeal(minHealing * 1.2f);
                    ability.setMaxDamageHeal(maxHealing * 1.2f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "+30% Healing",
                15000,
                () -> {
                    ability.setMinWaveHealing(minWaveHealing * 1.3f);
                    ability.setMaxWaveHealing(maxWaveHealing * 1.3f);
                    ability.setMinDamageHeal(minHealing * 1.3f);
                    ability.setMaxDamageHeal(maxHealing * 1.3f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "+40% Healing",
                20000,
                () -> {
                    ability.setMinWaveHealing(minWaveHealing * 1.4f);
                    ability.setMaxWaveHealing(maxWaveHealing * 1.4f);
                    ability.setMinDamageHeal(minHealing * 1.4f);
                    ability.setMaxDamageHeal(maxHealing * 1.4f);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "-5% Cooldown reduction\n+1 Energy per second",
                5000,
                () -> {
                    ability.setEnergyPerSecond(energyPerSecond + 1);
                    ability.setCooldown(cooldown * 0.95f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "-10% Cooldown reduction\n+2 Energy per second",
                10000,
                () -> {
                    ability.setEnergyPerSecond(energyPerSecond + 2);
                    ability.setCooldown(cooldown * 0.9f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "-15% Cooldown reduction\n+3 Energy per second",
                15000,
                () -> {
                    ability.setEnergyPerSecond(energyPerSecond + 3);
                    ability.setCooldown(cooldown * 0.85f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "-20% Cooldown reduction\n+4 Energy per second",
                20000,
                () -> {
                    ability.setEnergyPerSecond(energyPerSecond + 4);
                    ability.setCooldown(cooldown * 0.8f);
                }
        ));

        masterUpgrade = new Upgrade(
                "Liquor Of Life",
                "Vitality Liquor - Master Upgrade",
                "Double the impact range and the duration of the bonus energy per second of Vitality Liquor. Additionally, all enemies hit are slowed by 30% for 3 seconds.",
                50000,
                () -> {
                    ability.setVitalityRange(vitalityRange * 2);
                    ability.setDuration(ability.getDuration() * 2);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Medicinal Brew",
                "Vitality Liquor - Master Upgrade",
                """
                        Allies healed by Vitality, including the Apothecary, are given 30% speed and double energy duration.
                        """,
                50000,
                () -> {
                    ability.setDuration(ability.getDuration() * 2);
                }
        );
    }
}
