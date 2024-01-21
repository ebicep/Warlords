package com.ebicep.warlords.pve.upgrades.rogue.apothecary;

import com.ebicep.warlords.abilities.SoothingElixir;
import com.ebicep.warlords.pve.upgrades.*;

public class SoothingElixirBranch extends AbstractUpgradeBranch<SoothingElixir> {

    float minHealing = ability.getMinDamageHeal();
    float maxHealing = ability.getMaxDamageHeal();

    public SoothingElixirBranch(AbilityTree abilityTree, SoothingElixir ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgrade(new UpgradeTypes.HealingUpgradeType() {
                    @Override
                    public void run(float value) {
                        value = 1 + value / 100;
                        ability.setMinDamageHeal(minHealing * value);
                        ability.setMaxDamageHeal(maxHealing * value);
                    }
                }, 7.5f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeHitBox(ability, 1.5f)
                .addUpgradeDuration(ability, 40f, 4)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Alleviating Elixir",
                "Soothing Elixir - Master Upgrade",
                "Soothing Puddle now heals every 0.5s instead of 1s within the puddle radius and inflicts LEECH on all enemies hit by the elixir impact.",
                50000,
                () -> {

                }
        );
        masterUpgrade2 = new Upgrade(
                "Healing Elixir",
                "Soothing Elixir - Master Upgrade",
                """
                        Allies hit by Soothing Elixir, including the caster, have all current debuffs removed and become immune to debuffs for 4s. Additionally, for every target hit, increase the users max HP by 1.5% (max 25%) for 4s.
                        """,
                50000,
                () -> {

                }
        );
    }
}
