package com.ebicep.warlords.pve.upgrades.shaman.earthwarden;

import com.ebicep.warlords.abilities.HealingTotem;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeTreeBuilder;

public class HealingTotemBranch extends AbstractUpgradeBranch<HealingTotem> {

    public HealingTotemBranch(AbilityTree abilityTree, HealingTotem ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeHitBox(ability, 2)
                .addUpgradeHealing(ability.getHealValues().getTotemHealing(), 20f, 4)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeCooldown(ability)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Healing Obelisk",
                "Healing Totem - Master Upgrade",
                """
                        Double the duration of Healing Totem and reduce the decremental healing by 15%.

                        All enemies within the radius of Healing Totem are perpetually slowed and crippled, reducing their movement speed and damage dealt by 50%. Additionally, reduce their passive damage resistance by 2% for each second they are in range of your Healing Totem.""",
                50000,
                () -> {
                    ability.setHealingIncrement(ability.getHealingIncrement() - 15);
                    ability.setTickDuration(ability.getTickDuration() * 2);

                }
        );
        masterUpgrade2 = new Upgrade(
                "Void Totem",
                "Healing Totem - Master Upgrade",
                """
                        Enemies are perpetually sucked into your healing totem every second. Enemies within 4 blocks of the Totem take 750-900 damage every second. The totem will explode at the end removing 20% damage resistance and dealing high damage.
                        """,
                50000,
                () -> {
                }
        );
    }
}
