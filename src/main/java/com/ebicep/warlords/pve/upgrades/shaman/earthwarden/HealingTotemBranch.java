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

                        All enemies within the radius of Healing Totem are perpetually slowed and crippled, reducing their movement speed and damage dealt by 50%. Additionally, reduce their passive damage resistance by 5% for each second they are in range of your Healing Totem.""",
                50000,
                () -> {
                    ability.setHealingIncrement(ability.getHealingIncrement() - 15);
                    ability.setTickDuration(ability.getTickDuration() * 2);

                }
        );
        masterUpgrade2 = new Upgrade(
                "Resurgent Artifact",
                "Healing Totem - Master Upgrade",
                """
                        Healing Totem's crit chance and crit multiplier is increased by 25%, has double the duration and radius, and its decremental healing is reduced by 7.5%.
                                                
                        Allies within the range of totem have their own weapons imbued with Earthliving Weapon for the duration totem is active.
                        """,
                50000,
                () -> {
                    ability.getHealValues().getTotemHealing().critChance().addAdditiveModifier("Master Upgrade Branch", 25);
                    ability.getHealValues().getTotemHealing().critMultiplier().addAdditiveModifier("Master Upgrade Branch", 25);
                    ability.setHealingIncrement(ability.getHealingIncrement() - 7.5f);
                    ability.setTickDuration(ability.getTickDuration() * 2);
                    ability.getHitBoxRadius().addMultiplicativeModifierMult("Master Upgrade Branch", 2);
                }
        );
    }
}
