package com.ebicep.warlords.pve.upgrades.warrior;

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
                "+5% Damage",
                5000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.05f);
                    ability.setMaxDamageHeal(maxDamage * 1.05f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "+10% Damage",
                10000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.1f);
                    ability.setMaxDamageHeal(maxDamage * 1.1f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "+15% Damage",
                15000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.15f);
                    ability.setMaxDamageHeal(maxDamage * 1.15f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "+20% Damage",
                20000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.2f);
                    ability.setMaxDamageHeal(maxDamage * 1.2f);
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
                "Zeal - Tier II",
                "-7.5% Cooldown reduction",
                10000,
                () -> {
                    ability.setCooldown(cooldown * 0.925f);
                }
        ));
        treeB.add(new Upgrade(
                "Zeal - Tier III",
                "-11.25% Cooldown reduction",
                15000,
                () -> {
                    ability.setCooldown(cooldown * 0.8875f);
                }
        ));
        treeB.add(new Upgrade(
                "Zeal - Tier IV",
                "-15% Cooldown reduction",
                20000,
                () -> {
                    ability.setCooldown(cooldown * 0.85f);
                }
        ));

        masterUpgrade = new Upgrade(
                "Seismic Smash",
                "Seismic Wave - Master Upgrade",
                "Increase the size of Seismic Wave by 150% and deal increased damage the further away the enemy is. (Max 1.5x at 15 blocks).",
                50000,
                () -> {
                    ability.setPveMasterUpgrade(true);
                    ability.setWaveSize((int) (ability.getWaveSize() * 2.5f));
                    ability.setWaveWidth((int) (ability.getWaveWidth() * 2.5f));
                }
        );
    }
}
