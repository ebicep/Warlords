package com.ebicep.warlords.pve.upgrades.warrior.berserker;

import com.ebicep.warlords.abilties.SeismicWave;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class SeismicWaveBranch extends AbstractUpgradeBranch<SeismicWave> {

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();
    float cooldown = ability.getCooldown();

    public SeismicWaveBranch(AbilityTree abilityTree, SeismicWave ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade(
                "Impair - Tier I",
                "+3.75% Damage",
                5000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.0375f);
                    ability.setMaxDamageHeal(maxDamage * 1.0375f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "+8% Damage",
                10000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.08f);
                    ability.setMaxDamageHeal(maxDamage * 1.08f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "+11.25% Damage",
                15000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.1125f);
                    ability.setMaxDamageHeal(maxDamage * 1.1125f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "+15% Damage",
                20000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.15f);
                    ability.setMaxDamageHeal(maxDamage * 1.15f);
                }
        ));

        treeB.add(new Upgrade(
                "Zeal - Tier I",
                "-3.75% Cooldown reduction",
                5000,
                () -> {
                    ability.setCooldown(cooldown * 0.9725f);
                }
        ));
        treeB.add(new Upgrade(
                "Impair - Tier II",
                "-8% Cooldown reduction",
                10000,
                () -> {
                    ability.setCooldown(cooldown * 0.92f);
                }
        ));
        treeB.add(new Upgrade(
                "Impair - Tier III",
                "-11.25% Cooldown reduction",
                15000,
                () -> {
                    ability.setCooldown(cooldown * 0.8875f);
                }
        ));
        treeB.add(new Upgrade(
                "Impair - Tier IV",
                "-15% Cooldown reduction",
                20000,
                () -> {
                    ability.setCooldown(cooldown * 0.85f);
                }
        ));

        masterUpgrade = new Upgrade(
                "Seismic Smash",
                "Seismic Wave - Master Upgrade",
                "Increase energy cost by 50% but increase the\nsize of Seismic Wave by 100%",
                50000,
                () -> {
                    ability.setEnergyCost(ability.getEnergyCost() * 1.5f);
                    ability.setWaveSize(ability.getWaveSize() * 2);
                    ability.setWaveWidth(ability.getWaveWidth() * 2);
                }
        );
    }
}
