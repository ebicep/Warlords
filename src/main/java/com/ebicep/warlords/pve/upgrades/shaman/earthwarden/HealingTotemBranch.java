package com.ebicep.warlords.pve.upgrades.shaman.earthwarden;

import com.ebicep.warlords.abilities.HealingTotem;
import com.ebicep.warlords.pve.upgrades.*;

public class HealingTotemBranch extends AbstractUpgradeBranch<HealingTotem> {

    float minHealing = ability.getMinDamageHeal();
    float maxHealing = ability.getMaxDamageHeal();

    public HealingTotemBranch(AbilityTree abilityTree, HealingTotem ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeHitBox(ability, 2)
                .addUpgrade(new UpgradeTypes.HealingUpgradeType() {
                    @Override
                    public void run(float value) {
                        value = 1 + value / 100;
                        ability.setMinDamageHeal(minHealing * value);
                        ability.setMaxDamageHeal(maxHealing * value);
                    }
                }, 20f, 4)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeCooldown(ability)
                .addTo(treeB);

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

                }
        );
        masterUpgrade2 = new Upgrade(
                "Resurgent Artifact",
                "Healing Totem - Master Upgrade",
                """
                        Healing Totem's crit chance and crit multiplier is increased by 25% and has double the duration and radius.
                                                
                        Allies within the range of totem have their own weapons imbued with Earthliving Weapon for the duration totem is active.
                        """,
                50000,
                () -> {
                    ability.setCritChance(ability.getCritChance() + 25);
                    ability.setCritMultiplier(ability.getCritMultiplier() + 25);
                    ability.setTickDuration(ability.getTickDuration() * 2);
                    ability.getHitBoxRadius().addMultiplicativeModifierMult("Master Upgrade Branch", 2);
                }
        );
    }
}
